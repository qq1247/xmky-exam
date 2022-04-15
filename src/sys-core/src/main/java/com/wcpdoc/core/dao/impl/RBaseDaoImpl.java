package com.wcpdoc.core.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.apache.commons.text.CaseUtils;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.SqlUtil;

/**
 * 关系型数据访问层实现
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 * 当程序报错,而打断点只看到程序结束,而又没有异常,用try/catch. 原因:
 * spring jar包.AopUtils类.invokeJoinpointUsingReflection方法片段 
 * 	Use reflection to invoke the method.
 *	try {
 *		ReflectionUtils.makeAccessible(method);
 *		return method.invoke(target, args);
 *	}
 *	catch (InvocationTargetException ex) {
 *		// Invoked method threw a checked exception.
 *		// We must rethrow it. The client won't see the interceptor.
 *		throw ex.getTargetException();
 *	}
 * 
 * @param <T>
 */
public abstract class RBaseDaoImpl<T> implements RBaseDao<T> {
	@Resource
	private EntityManager entityManager;
	protected Class<T> clazz;

	@SuppressWarnings("unchecked")
	public RBaseDaoImpl() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
	}

	@Override
	public /*final*/ void add(T entity) {
		getCurSession().save(entity);
	}

	@Override
	public /*final*/ void update(T entity) {
		getCurSession().update(entity);
	}

	@Override
	public /*final*/ void del(Object id) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM ").append(clazz.getName()).append(" WHERE ID = :ID");
		@SuppressWarnings("unchecked")
		Query<T> query = getCurSession().createQuery(hql.toString());
		query.setParameter("ID", id);
		query.executeUpdate();
	}

	@Override
	public /*final*/ void del(Object[] ids) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM ").append(clazz.getName()).append(" WHERE ID IN (:IDS)");
		@SuppressWarnings("unchecked")
		Query<T> query = getCurSession().createQuery(hql.toString());
		query.setParameterList("IDS", ids);
		query.executeUpdate();
	}

	@Override
	public /*final*/ T getEntity(Object id) {
		if (id instanceof Integer) {
			return (T) getCurSession().get(clazz, (Integer) id);
		}
		if (id instanceof Long) {
			return (T) getCurSession().get(clazz, (Long) id);
		}
		if (id instanceof String) {
			return (T) getCurSession().get(clazz, (String) id);
		}

		throw new MyException("ID只支持Integer、Long、String类型");
	}
	
	@Override
	public /*final*/ int update(String sql, Object... parms) {
		List<String> namedParmList = SqlUtil.parseNamedParm(sql, parms);
		@SuppressWarnings("unchecked")
		Query<T> query = getCurSession().createSQLQuery(sql);
		for(int i = 0; i < parms.length; i++) {
			query.setParameter(namedParmList.get(i), parms[i]);
		}
		return query.executeUpdate();
	}

	@Override
	public /*final*/ int getCount(String sql, Object... parms) {
		List<String> namedParmList = SqlUtil.parseNamedParm(sql, parms);
		@SuppressWarnings("unchecked")
		Query<BigInteger> query = getCurSession().createSQLQuery(sql);
		for(int i = 0; i < parms.length; i++) {
			query.setParameter(namedParmList.get(i), parms[i]);
		}
		return query.uniqueResult().intValue();
	}
	
	@Override
	public abstract PageOut getListpage(PageIn pageIn);

	@SuppressWarnings("unchecked")
	@Override
	public /* final */ PageOut getListpage(SqlUtil sqlUtil, PageIn pageIn) {
		// 查询总记录数
		Query<Map<String, Object>> query = getCurSession().createSQLQuery(sqlUtil.getCountSql());
		List<Object> parmList = new ArrayList<>();
		parmList.addAll(sqlUtil.getFromParmList());
		parmList.addAll(sqlUtil.getWhereParmList());
		Object[] parms = parmList.toArray(new Object[parmList.size()]);
		List<String> namedParmList = SqlUtil.parseNamedParm(sqlUtil.getSql(), parms);
		for (int i = 0; i < parms.length; i++) {
			query.setParameter(namedParmList.get(i), parms[i]);
		}
		int total = ((BigInteger) query.uniqueResult()).intValue();
		if (total == 0) {
			return new PageOut(new ArrayList<Map<String,Object>>(), 0);// 如果总数为0，就不用在查询分页数据了
		}
				
		// 查询列表
		query = getCurSession().createSQLQuery(sqlUtil.getSql());
		for (int i = 0; i < parms.length; i++) {
			query.setParameter(namedParmList.get(i), parms[i]);
		}
		query.setFirstResult((pageIn.getCurPage() - 1) * pageIn.getPageSize());
		query.setMaxResults(pageIn.getPageSize());
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = query.list();
		
		// 封装结果
		return new PageOut(toCamelCase(result), total);
	}

	@Override
	public /*final*/ List<Map<String, Object>> getMapList(String sql, Object[] parms) {
		List<String> namedParmList = SqlUtil.parseNamedParm(sql, parms);
		@SuppressWarnings("unchecked")
		Query<Map<String, Object>> query = getCurSession().createSQLQuery(sql);
		for(int i = 0; i < parms.length; i++) {
			query.setParameter(namedParmList.get(i), parms[i]);
		}
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = query.list();
		return toCamelCase(result);
	}
	
	@Override
	public /*final*/ List<Map<String, Object>> getMapList(String sql) {
		return getMapList(sql, new Object[0]);
	}
	
	@Override
	public /*final*/ List<T> getList(String sql) {
		return getList(sql, new Object[0], clazz);
	}
	
	@Override
	public /*final*/ List<T> getList(String sql, Class<T> clazz) {
		return getList(sql, new Object[0], clazz);
	}

	@Override
	public /*final*/ List<T> getList(String sql, Object[] parms) {
		return getList(sql, parms, clazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public /*final*/ <E> List<E> getList(String sql, Object[] parms, Class<E> clazz) {
		List<String> namedParmList = SqlUtil.parseNamedParm(sql, parms);
		Query<T> query = getCurSession().createSQLQuery(sql);
		for (int i = 0; i < parms.length; i++) {
			query.setParameter(namedParmList.get(i), parms[i]);
		}

		query.unwrap(NativeQuery.class).addEntity(clazz);
		return (List<E>) query.list();
	}

	@Override
	public /*final*/ T getEntity(String sql, Object[] parms) {
		return getEntity(sql, parms, clazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public /*final*/ <E> E getEntity(String sql, Object[] parms, Class<E> clazz) {
		List<String> namedParmList = SqlUtil.parseNamedParm(sql, parms);
		Query<T> query = getCurSession().createSQLQuery(sql);
		for (int i = 0; i < parms.length; i++) {
			query.setParameter(namedParmList.get(i), parms[i]);
		}

		query.unwrap(NativeQuery.class).addEntity(clazz);
		return (E) query.uniqueResult();
	}

	@Override
	public /*final*/ Session getCurSession() {
		return entityManager.unwrap(Session.class);
	}

	@Override
	public /*final*/ void flush() {
		getCurSession().flush();
	}

	@Override
	public /*final*/ void evict(Object obj) {
		getCurSession().evict(obj);
	}
	
	/**
	 * 数据结果转为驼峰样式
	 * 大写转小写，下划线后一位转大写
	 * 
	 * v1.0 zhanghc 2022年4月11日下午2:54:25
	 * @param result void
	 */
	private List<Map<String, Object>> toCamelCase(List<Map<String, Object>> result) {
		for (Map<String, Object> map : result) {
			Set<String> keySet = new HashSet<>(map.keySet());
			for (String key : keySet) {
				map.put(CaseUtils.toCamelCase(key, false, new char[] { '_' }), map.remove(key));
			}
		}
		return result;
	}
}
