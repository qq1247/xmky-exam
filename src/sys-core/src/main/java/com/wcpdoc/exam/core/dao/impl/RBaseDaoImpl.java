package com.wcpdoc.exam.core.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcpdoc.exam.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.SqlUtil;

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
	private static final Logger log = LoggerFactory.getLogger(RBaseDaoImpl.class);
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
	public /*final*/ int update(String sql, Object... params) {
		@SuppressWarnings("unchecked")
		Query<T> query = getCurSession().createSQLQuery(toHibernateSql(sql));
		for(int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		
		return query.executeUpdate();
	}

	@Override
	public /*final*/ int getCount(String sql, Object... params) {
		sql = toHibernateSql(sql);
		@SuppressWarnings("unchecked")
		Query<BigInteger> query = getCurSession().createSQLQuery(sql);
		if (params != null) {
			for (int i = 0; i < params.length; i++) {
				query.setParameter(i, params[i]);
			}
		}
		return query.uniqueResult().intValue();
	}
	
	@Override
	public abstract PageOut getListpage(PageIn pageIn);

	@SuppressWarnings("unchecked")
	@Override
	public /*final*/ PageOut getListpage(SqlUtil sqlUtil, PageIn pageIn) {
		// 查询列表
		log.debug("Hibernate：{}", sqlUtil.getWhereParams());
		String sql = toHibernateSql(sqlUtil.getSql());
		Query<Map<String, Object>> query = getCurSession().createSQLQuery(sql);
		for (int i = 0; i < sqlUtil.getWhereParams().size(); i++) {
			query.setParameter(i, sqlUtil.getWhereParams().get(i));
		}
		query.setFirstResult((pageIn.getCurPage() - 1) * pageIn.getPageSize());
		query.setMaxResults(pageIn.getPageSize());
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = query.list();
		for(Map<String, Object> map : result){
			Set<String> keySet = new HashSet<>(map.keySet()); 
			for (String key : keySet) {
				map.put(CaseUtils.toCamelCase(key, false, new char[]{'_'}), map.remove(key)); 
			}
		}
		
		// 查询总记录数
		sql = toHibernateSql(sqlUtil.getCountSql());
		query = getCurSession().createSQLQuery(sql);
		for (int i = 0; i < sqlUtil.getWhereParams().size(); i++) {
			query.setParameter(i, sqlUtil.getWhereParams().get(i));
		}
		int total = ((BigInteger) query.uniqueResult()).intValue();

		// 封装结果
		return new PageOut(result, total);
	}
	
	@Override
	public /*final*/ List<Map<String, Object>> getMapList(String sql) {
		return getMapList(sql, new Object[0]);
	}

	@Override
	public /*final*/ List<Map<String, Object>> getMapList(String sql, Object[] params) {
		sql = toHibernateSql(sql);
		@SuppressWarnings("unchecked")
		Query<Map<String, Object>> query = getCurSession().createSQLQuery(sql);
		query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		log.debug("Hibernate：{}", params);
		return query.list();
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
	public /*final*/ List<T> getList(String sql, Object[] params) {
		return getList(sql, params, clazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public /*final*/ <E> List<E> getList(String sql, Object[] params, Class<E> clazz) {
		sql = toHibernateSql(sql);
		Query<T> query = getCurSession().createSQLQuery(sql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}

		query.unwrap(NativeQuery.class).addEntity(clazz);
		return (List<E>) query.list();
	}

	@Override
	public /*final*/ T getEntity(String sql, Object[] params) {
		return getEntity(sql, params, clazz);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public /*final*/ <E> E getEntity(String sql, Object[] params, Class<E> clazz) {
		sql = toHibernateSql(sql);
		Query<T> query = getCurSession().createSQLQuery(sql);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
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
	
	private String toHibernateSql(String sql) {
		StringBuilder _sql = new StringBuilder();
		int pos = 0;
		for (int i = 0; i < sql.length(); i++) {
			char c = sql.charAt(i);
			_sql.append(c);
			if ('?' == c) {
				_sql.append(pos++);
			}
		}
		return _sql.toString();
	}
}
