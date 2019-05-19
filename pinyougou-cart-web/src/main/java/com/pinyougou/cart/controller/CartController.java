package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;

/**
 * 后端控制层 1.从cookie中取出购物车 2.向购物车添加商品 3.将购物车存入cookie
 * 
 * @author 李帅辉
 *
 */
@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference(timeout=6000)
	private CartService cartService;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	/**
	 * 购物车列表
	 * 
	 * @return
	 */
	@RequestMapping("/findCartList")
	public List<Cart> findCartList() {
		// 当前登陆人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登陆人：" + username);
		
		String cartListString = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if (cartListString == null || cartListString.equals("")) {
			cartListString = "[]";
		}
		List<Cart> cartList_cookie = JSON.parseArray(cartListString, Cart.class);
		if (username.equals("anonymousUser")) { // 如果未登录
			// 1.从cookie提取购物车
			System.out.println("从cookie中提取购购物车信息...");			
			return cartList_cookie;
		} else { // 如果已登陆
			// 从redis中提取用户的购物车内容
			System.out.println("从redis中提取用户的购物车信息...");
			List<Cart> cartList_redis = cartService.findCartListFromRedis(username);
			
			if (cartList_cookie.size()>0) {
				//合并购物车逻辑
				List<Cart> cartList = cartService.mergeCartList(cartList_redis, cartList_cookie);
				//清除本地cookie的数据
				util.CookieUtil.deleteCookie(request, response, "cartList");
				//将合并后的购物车存入redis
				cartService.saveCartListToRedis(username, cartList_redis);
				System.out.println("执行购物车合并逻辑");
				return cartList;
			}			
			return cartList_redis;
		}

	}

	/**
	 * 添加商品到购物车
	 * 
	 * @param itemId
	 * @param num
	 * @return
	 */
	@RequestMapping("/addGoodsToCartList")
	@CrossOrigin(origins="http://localhost:9105",allowCredentials="true")
	public Result addGoodsToCartList(Long itemId, Integer num) {
		
		//response.setHeader("Access-Control-Allow-Origin","http://localhost:9105"); 
		//response.setHeader("Access-Control-Allow-Credentials","true"); 
		
		// 当前登陆人账号
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登陆用户：" + username);
		
		try {
			List<Cart> cartList = findCartList();// 获取购物车列表
			// 2.调用服务方法操作购物车
			cartList = cartService.addGoodsToCartList(cartList, itemId, num);
			if (username.equals("anonymousUser")) { // 如果未登录				
				// 3.将新的购物车存入cookie
				util.CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24,"UTF-8");
				System.out.println("向cookie中存入购物车信息...");
			}else{ // 如果已登陆
				// 向redis存入购物车
				System.out.println("向redis中存入购物车信息...");
				cartService.saveCartListToRedis(username, cartList);
				
			}
			return new Result(true, "添加到购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(true, "添加到购物车失败");
		}

	}
}
