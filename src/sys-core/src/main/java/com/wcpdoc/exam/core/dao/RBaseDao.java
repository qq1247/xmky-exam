package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

/**
 * 关系型数据访问层接口
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public interface RBaseDao<T> extends BaseDao<T> {
	
	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param sql
	 * @param params
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getMapList(String sql);
	
	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param sql
	 * @param params
	 * @return List<Map<String,Object>>
	 */
	public List<Map<String, Object>> getMapList(String sql, Object[] params);
	
	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param sql
	 * @param params
	 * @return List<T>
	 */
	public List<T> getList(String sql);
	
	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param sql
	 * @param params
	 * @return List<T>
	 */
	public List<T> getList(String sql, Class<T> clazz);
	
	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param sql
	 * @param params
	 * @return List<T>
	 */
	public List<T> getList(String sql, Object[] params);

	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @param <E>
	 * @param sql
	 * @param params
	 * @return List<T>
	 */
	public <E> List<E> getList(String sql, Object[] params, Class<E> clazz);
	
	/**
	 * 获取实体
	 * 
	 * v1.0 zhanghc 2016年5月18日下午9:09:25
	 * @param sql
	 * @param params
	 * @return Map<String,Object> 如果存在多条，返回第一条；如果未找到，返回null
	 */
	public T getEntity(String sql, Object[] params);
	
	/**
	 * 获取实体
	 * 
	 * v1.0 zhanghc 2016年5月18日下午9:09:25
	 * @param <E>
	 * @param sql
	 * @param params
	 * @return Map<String,Object> 如果存在多条，返回第一条；如果未找到，返回null
	 */
	public <E> E getEntity(String sql, Object[] params, Class<E> clazz);
	
	/**
	 * 用自定义sql更新或删除
	 * 
	 * v1.0 zhanghc 2015-7-21下午10:29:43
	 * @param sql
	 * @param params
	 * @return int 更新数量
	 */
	public int update(String sql, Object... params);
	
	/**
	 * 获取总数
	 * 
	 * v1.0 zhanghc 2016年5月18日下午9:09:25
	 * @param sql
	 * @param params
	 * @return int 总数
	 */
	public int getCount(String sql, Object... params);
	
	/**
	 * 获取当前线程上绑定的session
	 * 
	 * v1.0 zhanghc 2016年5月2日上午9:17:52
	 * @return Session
	 */
	public Session getCurSession();

	/**
	 * session数据同步到数据库
	 * 
	 * v1.0 zhanghc 2016年5月2日上午9:18:20 
	 * void
	 */
	public void flush();
	
	/**
	 * 清除缓存对象
	 * 
	 * v1.0 zhanghc 2016年7月13日下午4:07:04
	 * @param obj
	 * void
	 */
	public void evict(Object obj);
}
