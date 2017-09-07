package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;

public interface BaseDao<T> {
	public void save(T entity);

	public void update(T entity);

	public void del(Integer id);

	public void del(Integer[] ids);
	
	public T getEntity(Integer id);
	
	/**
	 * 获取当前线程上绑定的session
	 * v1.0 zhanghc 2016年5月2日上午9:17:52
	 * @return
	 * Session
	 */
	public Session getCurrentSession();
	
	/**
	 * 把当前线程的绑定的session缓存刷入数据库
	 * v1.0 zhanghc 2016年5月2日上午9:18:20
	 * void
	 */
	public void flush();

	/**
	 * 获取分页列表。
	 * v1.0 zhanghc 2016年5月2日上午10:26:49
	 * @param pageIn
	 * @return pageOut
	 */
	public PageOut getListpage(PageIn pageIn);

	/**
	 * 获取分页列表。
	 * v1.0 zhanghc 2016年5月8日下午3:18:45
	 * @param sqlUtil
	 * @param pageIn
	 * @return pageOut
	 */
	public PageOut getListpage(SqlUtil sqlUtil, PageIn pageIn);

	/**
	 * 返回列表
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param sql
	 * @param params
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getList(String sql, Object[] params);
	
	/**
	 * 返回列表
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @param sql
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getList(String sql);
	
	/**
	 * 返回列表
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param sql
	 * @param params
	 * @param t
	 * @return List<T>
	 */
	@SuppressWarnings("hiding")
	public <T> List<T> getList(String sql, Object[] params, Class<T> t);
	
	/**
	 * 返回列表
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param sql
	 * @param t
	 * @return List<T>
	 */
	@SuppressWarnings("hiding")
	public <T> List<T> getList(String sql, Class<T> t);
	
	/**
	 * 返回单条数据
	 * v1.0 zhanghc 2016年5月18日下午9:09:25
	 * @param sql
	 * @param params
	 * @return Map<String,Object> 如果存在多条，返回第一条；如果未找到，返回null
	 */
	public Map<String, Object> getUnique(String sql, Object[] params);

	/**
	 * 返回单条数据
	 * v1.0 zhanghc 2016年5月18日下午9:09:25
	 * @param sql
	 * @return Map<String,Object> 如果存在多条，返回第一条；如果未找到，返回null
	 */
	public Map<String, Object> getUnique(String sql);
	
	/**
	 * 返回单条数据
	 * v1.0 zhanghc 2016年5月18日下午9:09:25
	 * @param sql
	 * @param params
	 * @param t
	 * @return Map<String,Object> 如果存在多条，返回第一条；如果未找到，返回null
	 */
	@SuppressWarnings("hiding")
	public <T> T getUnique(String sql, Object[] params, Class<T> t);
	
	/**
	 * 返回单条数据
	 * v1.0 zhanghc 2016年5月18日下午9:09:25
	 * @param sql
	 * @param t
	 * @return Map<String,Object> 如果存在多条，返回第一条；如果未找到，返回null
	 */
	@SuppressWarnings("hiding")
	public <T> T getUnique(String sql, Class<T> t);
	
	/**
	 * 用自定义sql更新或删除
	 * v1.0 zhanghc 2015-7-21下午10:29:43
	 * 
	 * @param sql
	 * @param params
	 * @return int 更新数量
	 */
	public int update(String sql, Object... params);
	
	/**
	 * 清除hibernate缓存对象
	 * v1.0 zhanghc 2016年7月13日下午4:07:04
	 * @param obj
	 * void
	 */
	public void evict(Object obj);
}
