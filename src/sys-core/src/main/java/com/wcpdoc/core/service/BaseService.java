package com.wcpdoc.core.service;

import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 基础服务层接口
 * 
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
	public void del(Object id);

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param entity
	 */
	public void del(Object[] ids);

	/**
	 * 获取实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param entity
	 */
	public T getEntity(Object id);

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
	 * 获取当前登录用户
	 * 
	 * v1.0 zhanghc 2020年1月16日上午10:47:47
	 * @return LoginUser
	 */
	LoginUser getCurUser();
}
