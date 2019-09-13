package com.wcpdoc.exam.core.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;

/**
 * 数据访问层实现
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 * 
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
public abstract class BaseDaoImpl<T> implements BaseDao<T> {
	@Resource
	private EntityManager entityManager;
	protected Class<T> clazz;

	@SuppressWarnings("unchecked")
	public BaseDaoImpl() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
	}

	@Override
	public /* final */ void add(T entity) {
		getCurSession().save(entity);
	}

	@Override
	public /* final */ void update(T entity) {
		getCurSession().update(entity);
	}

	@Override
	public /* final */ void del(Integer id) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM ").append(clazz.getName()).append(" WHERE ID = :ID");
		@SuppressWarnings("unchecked")
		Query<T> query = getCurSession().createQuery(hql.toString());
		query.setParameter("ID", id);
		query.executeUpdate();
	}

	@Override
	public /* final */ void del(Integer[] ids) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM ").append(clazz.getName()).append(" WHERE ID IN (:IDS)");
		@SuppressWarnings("unchecked")
		Query<T> query = getCurSession().createQuery(hql.toString());
		query.setParameterList("IDS", ids);
		query.executeUpdate();
	}

	@Override
	public /* final */ T getEntity(Integer id) {
		return (T) getCurSession().get(clazz, id);
	}

	@Override
	public /* final */ Session getCurSession() {
		return entityManager.unwrap(Session.class);
	}

	@Override
	public /* final */ void flush() {
		getCurSession().flush();
	}

	@Override
	public abstract PageOut getListpage(PageIn pageIn);

	@Override
	public /* final */ PageOut getListpage(SqlUtil sqlUtil, PageIn pageIn) {
		// 查询列表
		@SuppressWarnings("unchecked")
		Query<Map<String, Object>> query = getCurSession().createSQLQuery(toHibernateSql(sqlUtil.getSql()));
		for (int i = 0; i < sqlUtil.getWhereParams().size(); i++) {
			query.setParameter(i, sqlUtil.getWhereParams().get(i));
		}
		query.setFirstResult((pageIn.getCurrentPage() - 1) * pageIn.getPageSize());
		query.setMaxResults(pageIn.getPageSize());
		((NativeQueryImpl<Map<String, Object>>) query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		List<Map<String, Object>> result = query.list();

		// 查询总记录数
		@SuppressWarnings("unchecked")
		Query<T> query1 = getCurSession().createSQLQuery(toHibernateSql(sqlUtil.getCountSql()));
		for (int i = 0; i < sqlUtil.getWhereParams().size(); i++) {
			query1.setParameter(i, sqlUtil.getWhereParams().get(i));
		}
		int total = ((BigInteger) query1.uniqueResult()).intValue();

		// 封装查询列表和总记录数，并返回
		return new PageOut(result, total);
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

	@Override
	@SuppressWarnings({ "unchecked", "hiding" })
	public /* final */ <T> List<T> getList(String sql, Object[] params, Class<T> t) {
		Query<T> query = getCurSession().createSQLQuery(toHibernateSql(sql));

		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}

		((NativeQuery<T>) query).addEntity(t);
		return query.list();
	}

	@Override
	@SuppressWarnings("hiding")
	public /* final */ <T> List<T> getList(String sql, Class<T> t) {
		return getList(sql, new Object[0], t);
	}

	@Override
	@SuppressWarnings("unchecked")
	public /* final */ List<Map<String, Object>> getList(String sql, Object[] params) {
		Query<Map<String, Object>> query = getCurSession().createSQLQuery(toHibernateSql(sql));
		((NativeQueryImpl<T>) query).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}
		return query.list();
	}

	@Override
	public /* final */ List<Map<String, Object>> getList(String sql) {
		return getList(sql, new Object[0]);
	}

	@Override
	public /* final */ Map<String, Object> getUnique(String sql, Object[] params) {
		List<Map<String, Object>> list = getList(sql, params);
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	@Override
	public /* final */ Map<String, Object> getUnique(String sql) {
		return getUnique(sql, new Object[0]);
	}

	@Override
	@SuppressWarnings("hiding")
	public /* final */ <T> T getUnique(String sql, Object[] params, Class<T> t) {
		List<T> list = getList(sql, params, t);
		if (list.size() == 0) {
			return null;
		}
		return list.get(0);
	}

	@Override
	@SuppressWarnings("hiding")
	public /* final */ <T> T getUnique(String sql, Class<T> t) {
		return getUnique(sql, new Object[0], t);
	}

	@Override
	public /* final */ int update(String sql, Object... params) {
		@SuppressWarnings("unchecked")
		Query<T> query = getCurSession().createSQLQuery(toHibernateSql(sql));
		for (int i = 0; i < params.length; i++) {
			query.setParameter(i, params[i]);
		}

		return query.executeUpdate();
	}

	@Override
	public void evict(Object obj) {
		getCurSession().evict(obj);
	}
}
