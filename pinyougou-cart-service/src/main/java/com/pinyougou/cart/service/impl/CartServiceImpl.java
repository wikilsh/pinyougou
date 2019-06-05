package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
/**
 * 购物车服务实现类
 * @author 李帅辉
 *
 */
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		
			
		//1.根据商品SKU ID查询SKU商品信息
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item == null) {
			throw new RuntimeException("商品不存在");
		}
		if (!item.getStatus().equals("1")) {
			throw new RuntimeException("商品无效");
		}
		
		//2.获取商家id
		String sellerId = item.getSellerId();
		
		//3.根据商家ID判断购物车是否存在该商家的购物车
		Cart cart = searchCartBySellerId(cartList, sellerId);
		
		//4.如果购物车不存在该商家的购物车
		if (cart==null) {
			//4.1新建购物车对象
			cart=new Cart();
			cart.setSellerId(sellerId);//商家ID
			cart.setSellerName(item.getSeller());//商家名称
			
			List<TbOrderItem> orderItemList=new ArrayList();//创建购物车明细列表
			//创建新的购物车列表的 明细对象
			TbOrderItem orderItem = createOrderItem(item, num);
			orderItemList.add(orderItem);
						
			cart.setOrderItemList(orderItemList);
			
			//4.2 将新建的购物车列表明细对象 添加到 	购物车列表
			cartList.add(cart);
		}else{
			//5.如果购物车列表中存在该商家的购物车			
			//查询购物车明细列表中是否有该商品
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(), itemId);
			if (orderItem==null) {
				//5.1 没有，新增购物车明细
				orderItem=createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
			}else {
				//5.2 有 在原购物车明细上添加数量，更改金额
				orderItem.setNum(orderItem.getNum()+num);
				orderItem.setTotalFee(new BigDecimal(orderItem.getNum()*orderItem.getPrice().doubleValue()) );
				//如果数量修改后<0  则移除此商品
				if (orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);//移除商品购物车明细
				}
				//如果移除后Cart的明细数量为0，则将cart移除
				if(cart.getOrderItemList().size()==0){
					cartList.remove(cart);
				}
			}			
		}
		
		
		return cartList;
	}
	/**
	 * 根据商家ID查询购物车对象
	 * @param cartList
	 * @param sellerId
	 * @return
	 */
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}

	/**
	 * 根据商品明细ID查询
	 * @param orderItemList
	 * @param itemId
	 * @return
	 */
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList,Long itemId) {
		for (TbOrderItem orderItem : orderItemList) {
			if (orderItem.getItemId().longValue()==itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}
	
	/**
	 * 创建订单明细
	 * @param item
	 * @param num
	 * @return
	 */
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		if (num<=0) {
			throw new RuntimeException("数量非法");
		}
		
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setItemId(item.getId());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTitle(item.getTitle());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		
		return orderItem;
	}
	/**
	 * 从redis中查询购物车
	 */
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public List<Cart> findCartListFromRedis(String userame) {
		System.out.println("从redis中提取用户购物车的数据..."+userame);
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userame);
		if (cartList==null) {
			cartList=new ArrayList();
		}
		return cartList;
	}
	
	/**
	 * 将用户的购物车保存到redis缓存中
	 * @param username
	 * @param cartList
	 */
	@Override
	public void saveCartListToRedis(String username, List<Cart> cartList) {		
		System.out.println("向redis存入用户的购物车数据..."+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
	}
	/**
	 * 购物车的合并
	 */
	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		System.out.println("合并购物车");
		for(Cart cart:cartList2){
			for(TbOrderItem orderItem:cart.getOrderItemList()){
				cartList1=addGoodsToCartList(cartList1, orderItem.getItemId(), orderItem.getNum());
			}
		}
		return cartList1;
	}
	
	@Autowired
	private TbAddressMapper addressMapper;
	@Override
	public void addAddress(TbAddress address) {
		addressMapper.insert(address); 
		
	}
}
