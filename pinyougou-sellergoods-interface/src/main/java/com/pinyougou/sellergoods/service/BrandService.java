package com.pinyougou.sellergoods.service;

import java.util.List;
import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

/**
 * 品牌接口 
 * @author 李帅辉
 *
 */
public interface BrandService {

	public List<TbBrand> findAll();
	
	/**
	 * 
	 * @param pageNum 当前页
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	/**
	 *品牌的增加 
	 * @param brand
	 */
	public void add(TbBrand brand);
	
	/**
	 * 根据id查询实体
	 * @param id
	 * @return
	 */
	public TbBrand findOne(Long id);
	
	/**
	 * 修改
	 * @param brand
	 */
	public void update(TbBrand brand);
	
	/**
	 * 批量删除品牌
	 * @param ids
	 */
	public void delete(Long[] ids);
	
	/**
	 * 品牌分页
	 * @param brand 实体类
	 * @param pageNum 当前页码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbBrand brand,int pageNum,int pageSize);
}
