package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 普通的增删改查不要再次封装，只提供最基本的功能就可以了。
 * 如新增用trycatch，新增失败就不应该继续执行，catch会造成问题。
 * 
 * v1.0 zhanghc 2016年5月1日下午10:21:10
 * 
 * @param <T>
 */
public interface BaseService<T> {
	/**
	 * 添加实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param entity
	 */
	public void add(T entity);

	/**
	 * 修改实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param entity
	 */
	public void update(T entity);

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param entity
	 */
	public void del(Integer id);

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param entity
	 */
	public void del(Integer[] ids);

	/**
	 * 获取实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param entity
	 */
	public T getEntity(Integer id);

	/**
	 * 获取分页列表。
	 * 
	 * v1.0 zhanghc 2016年5月2日上午10:26:49
	 * 
	 * @param pageIn
	 * @return pageOut
	 */
	public PageOut getListpage(PageIn pageIn);

	/**
	 * 清除hibernate缓存对象
	 * 
	 * v1.0 zhanghc 2016年7月13日下午4:07:04
	 * 
	 * @param obj
	 * void
	 */
	public void evict(Object obj);
}
