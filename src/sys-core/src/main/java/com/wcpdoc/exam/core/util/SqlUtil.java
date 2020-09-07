package com.wcpdoc.exam.core.util;

import java.util.ArrayList;
import java.util.List;

/**
 * sql工具类
 * 
 * @author zhanghc 2015-3-27下午04:58:24
 */
public class SqlUtil {
	/**
	 * 排序枚举，用于获取asc或desc字符串。
	 * 
	 * @author zhanghc 2015-3-30下午03:46:14
	 */
	public enum Order {
		ASC("ASC"), DESC("DESC");

		private String value;

		private Order(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	private StringBuilder from = null;
	private StringBuilder where = new StringBuilder();
	private StringBuilder order = new StringBuilder();
	private List<Object> whereParams = new ArrayList<Object>();

	public SqlUtil(String from) {
		this.from = new StringBuilder(from);
	}

	/**
	 * 添加where条件
	 * 
	 * v1.0 zhanghc 2018年11月26日上午12:00:41
	 * 
	 * @param condition
	 * return SqlUtil
	 */
	public SqlUtil addWhere(String condition) {
		if (where.length() == 0) {
			where.append(" where ").append(condition);
		} else {
			where.append(" and ").append(condition);
		}
		return this;
	}

	/**
	 * 添加where条件
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:14:25
	 * 
	 * @param condition
	 * @param params
	 * @return SqlUtil
	 */
	public SqlUtil addWhere(String condition, Object... params) {
		addWhere(condition);

		if (params != null && params.length > 0) {
			for (Object param : params) {
				whereParams.add(param);
			}
		}
		return this;
	}

	/**
	 * 添加where条件
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:14:25
	 * 
	 * @param result
	 * @param condition
	 * @param params
	 * @return SqlUtil
	 */
	public SqlUtil addWhere(boolean result, String condition, Object... params) {
		if (result) {
			addWhere(condition, params);
		}
		return this;
	}

	/**
	 * 添加order排序
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:14:53
	 * 
	 * @param property
	 * @param order
	 * @return SqlUtil
	 */
	public SqlUtil addOrder(String property, Order order) {
		if (this.order.length() == 0) {
			this.order.append(" order by ").append(property).append(" ").append(order.getValue());
		} else {
			this.order.append(",  ").append(property).append(" ").append(order.getValue());
		}
		return this;
	}

	/**
	 * 添加order排序
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:14:53
	 * 
	 * @param result
	 * @param property
	 * @param order
	 * @return SqlUtil
	 */
	public SqlUtil addOrder(boolean result, String property, Order order) {
		if (result) {
			addOrder(property, order);
		}
		return this;
	}

	/**
	 * 获取sql
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:15:18
	 * 
	 * @return String
	 */
	public String getSql() {
		StringBuilder sql = new StringBuilder();
		sql.append(from).append(where).append(order);
		return sql.toString();
	}

	/**
	 * 获取总记录数的sql
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:15:37
	 * 
	 * @return String
	 */
	public String getCountSql() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(*) FROM ( ").append(from).append(where).append(" ) C");
		return sql.toString();
	}

	/**
	 * 返回where语句的参数
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:16:44
	 * 
	 * @return List<Object>
	 */
	public List<Object> getWhereParams() {
		return whereParams;
	}

	@Override
	public String toString() {
		return getSql();
	}
}
