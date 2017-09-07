package com.wcpdoc.exam.core.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;

/**
 * 当程序报错,而打断点只看到程序结束,而又没有异常,用try/catch. 原因: spring jar包.AopUtils类.invokeJoinpointUsingReflection方法片段 try {
 * ReflectionUtils.makeAccessible(method); return method.invoke(target, args); } catch (InvocationTargetException ex) {
 * // Invoked method threw a checked exception. 回调方法抛出了一检测时异常 // We must rethrow it. The client won't see the
 * interceptor.我们必须重新抛出它.客户端不会看到这个拦截. throw ex.getTargetException(); //重新抛出异常,但没有这个跟spring的理念有关系,看spring参考手册. }
 * @param <T>
 */
public abstract class BaseDaoImpl<T> implements BaseDao<T> {
	@Resource
	private SessionFactory sessionFactory;
	protected Class<T> clazz;

	@SuppressWarnings("unchecked")
	public BaseDaoImpl() {
		ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
		this.clazz = (Class<T>) pt.getActualTypeArguments()[0];
	}

	@Override
	public final void save(T entity) {
		getCurrentSession().save(entity);
	}

	@Override
	public final void update(T entity) {
		getCurrentSession().update(entity);
	}

	@Override
	public final void del(Integer id) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM ").append(clazz.getName()).append(" WHERE ID = :ID");
		Query query = getCurrentSession().createQuery(hql.toString());
		query.setParameter("ID", id);
		query.executeUpdate();
	}

	@Override
	public final void del(Integer[] ids) {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE FROM ").append(clazz.getName()).append(" WHERE ID IN (:IDS)");
		Query query = getCurrentSession().createQuery(hql.toString());
		query.setParameterList("IDS", ids);
		query.executeUpdate();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final T getEntity(Integer id) {
		return (T) getCurrentSession().get(clazz, id);
	}

	@Override
	public final Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public final void flush() {
		getCurrentSession().flush();
	}

	@Override
	public abstract PageOut getListpage(PageIn pageIn);
	
	@Override
	public final PageOut getListpage(SqlUtil sqlUtil, PageIn pageIn) {
		// 查询列表
		SQLQuery query = getCurrentSession().createSQLQuery(sqlUtil.getSql());
		for (int i = 0; i < sqlUtil.getWhereParams().size(); i++) {
			query.setParameter(i, sqlUtil.getWhereParams().get(i));
		}
		query.setFirstResult((pageIn.getCurrentPage() - 1) * pageIn.getPageSize());
		query.setMaxResults(pageIn.getPageSize());
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> result = query.list();

		// 查询总记录数
		query = getCurrentSession().createSQLQuery(sqlUtil.getCountSql());
		for (int i = 0; i < sqlUtil.getWhereParams().size(); i++) {
			query.setParameter(i, sqlUtil.getWhereParams().get(i));
		}
		int total = ((BigInteger) query.uniqueResult()).intValue();

		//封装查询列表和总记录数，并返回
		return new PageOut(result, total);
	}

	@Override
	@SuppressWarnings({ "unchecked", "hiding" })
	public final <T> List<T> getList(String sql, Object[] params, Class<T> t) {
		SQLQuery query = getCurrentSession().createSQLQuery(sql);
		
		for(int i = 0; i < params.length; i++){
			query.setParameter(i, params[i]);
		}
		
		query.addEntity(t);
		return query.list();
	}

	@Override
	@SuppressWarnings("hiding")
	public final <T> List<T> getList(String sql, Class<T> t) {
		return getList(sql, new Object[0], t);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public final List<Map<String, Object>> getList(String sql, Object[] params) {
		SQLQuery query = getCurrentSession().createSQLQuery(sql);
		query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		for(int i = 0; i < params.length; i++){
			query.setParameter(i, params[i]);
		}
		return query.list();
	}

	@Override
	public final List<Map<String, Object>> getList(String sql) {
		return getList(sql, new Object[0]);
	}

	@Override
	public final Map<String, Object> getUnique(String sql, Object[] params) {
		List<Map<String, Object>> list = getList(sql, params);
		if(list.size() == 0){
			return null;
		}
		return list.get(0);
	}

	@Override
	public final Map<String, Object> getUnique(String sql) {
		return getUnique(sql, new Object[0]);
	}

	@Override
	@SuppressWarnings("hiding")
	public final <T> T getUnique(String sql, Object[] params, Class<T> t) {
		List<T> list = getList(sql, params, t);
		if(list.size() == 0){
			return null;
		}
		return list.get(0);
	}

	@Override
	@SuppressWarnings("hiding")
	public final <T> T getUnique(String sql, Class<T> t) {
		return getUnique(sql, new Object[0], t);
	}

	@Override
	public final int update(String sql, Object... params) {
		SQLQuery query = getCurrentSession().createSQLQuery(sql);
		for(int i = 0; i < params.length; i++){
			query.setParameter(i, params[i]);
		}
		
		return query.executeUpdate();
	}

	@Override
	public void evict(Object obj) {
		getCurrentSession().evict(obj);
	}
}
