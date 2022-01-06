package com.wcpdoc.core.dao;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;

/**
 * 数据访问层接口
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public interface BaseDao<T> {
	/**
	 * 添加实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param entity 
	 * void
	 */
	public void add(T entity);

	/**
	 * 修改实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param entity 
	 * void
	 */
	public void update(T entity);

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param id 
	 * void
	 */
	public void del(Object id);

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param ids 
	 * void
	 */
	public void del(Object[] ids);

	/**
	 * 获取实体
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param id
	 * @return T
	 */
	public T getEntity(Object id);
	
	/**
	 * 获取分页列表
	 * 
	 * v1.0 zhanghc 2016年5月2日上午10:26:49
	 * @param pageIn
	 * @return pageOut
	 */
	public PageOut getListpage(PageIn pageIn);

	/**
	 * 获取分页列表
	 * 
	 * v1.0 zhanghc 2016年5月8日下午3:18:45
	 * @param sqlUtil
	 * @param pageIn
	 * @return pageOut
	 */
	public PageOut getListpage(SqlUtil sqlUtil, PageIn pageIn);
}
