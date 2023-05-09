package com.wcpdoc.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wcpdoc.core.exception.MyException;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * sql工具类
 * 
 * @author zhanghc 2015-3-27下午04:58:24
 */
public class SqlUtil {
	public static final Pattern NAMED_PARM_PATTERN = Pattern.compile(":[A-Za-z0-9_.]+");
	
	private StringBuilder from = new StringBuilder();
	private StringBuilder where = new StringBuilder();
	private StringBuilder order = new StringBuilder();
	private List<Object> fromParmList = new ArrayList<>();
	private List<Object> whereParmList = new ArrayList<>();
	
	private static final SelectExpressionItem COLUMN = new SelectExpressionItem(new Column("COUNT(*)"));// 全局唯一
	private static final List<SelectItem> COLUMN_LIST = new ArrayList<>();
	static {
		COLUMN_LIST.add(COLUMN);
	}

	public SqlUtil(String from) {
		this.from.append(from);
	}
	
	/**
	 * 添加from参数
	 * 
	 * v1.0 zhanghc 2022年4月12日下午3:34:53
	 * @param fromParm
	 * @return SqlUtil
	 */
	public SqlUtil addFromParm(Object fromParm) {
		fromParmList.add(fromParm);
		return this;
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
		where.append(where.length() == 0 ? " WHERE " : " AND ").append(condition);
		return this;
	}

	/**
	 * 添加where条件
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:14:25
	 * 
	 * @param condition
	 * @param parms
	 * @return SqlUtil
	 */
	public SqlUtil addWhere(String condition, Object... parms) {
		// 添加where条件
		addWhere(condition);
		
		// 添加where参数
		Collections.addAll(whereParmList, parms);
//		for (int i = 0; i < parms.length; i++) {
//			whereParmList.add(new WhereParm(namedParmList.get(i), parms[i]));
//		}
		return this;
	}
	
	/**
	 * 添加where条件
	 * 
	 * v1.0 zhanghc 2015-3-27下午05:14:25
	 * 
	 * @param result
	 * @param condition
	 * @param parms
	 * @return SqlUtil
	 */
	public SqlUtil addWhere(boolean result, String condition, Object... parms) {
		if (result) {
			addWhere(condition, parms);
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
		this.order.append(this.order.length() == 0 ? " ORDER BY " : ",  ")
				.append(property).append(" ").append(order.getValue());
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
		sql.append(from).append(where);
		
		Select select;
		try {
			select = (Select) CCJSqlParserUtil.parse(sql.toString());
		} catch (JSQLParserException e) {
			throw new MyException(e);
		}
		PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
		plainSelect.setSelectItems(COLUMN_LIST);
		return select.toString();
	}

	/**
	 * 解析命名参数
	 * 
	 * 为保持方法调用简便性，命名参数不允许重复<br/>
	 * parseNamedParm(""); parms = new Object[0];<br/>
	 * parseNamedParm("", null); parms = null;<br/>
	 * parseNamedParm("", null, null); parms = new Object[]{null, null};<br/>
	 * parseNamedParm("", new String[0]); parms = new String[0];<br/>
	 * parseNamedParm("", new String[0], new String[0]); parms = new Object[]{new String[0], new String[0]};<br/>
	 * parseNamedParm("", new Object[]{1, "2"}); parms = new Object[]{1, "2"};<br/>
	 * 
	 * v1.0 zhanghc 2022年4月11日上午10:44:23
	 * @param sql
	 * @param parms
	 * @return List<String>
	 */
	public static List<String> parseNamedParm(String sql, Object... parms) {
		if (!ValidateUtil.isValid(sql)) {
			throw new MyException("参数错误：sql不能为空");
		}
		if (parms == null) {
//		if (!ValidateUtil.isValid(parms)) {// 长度为0也算有
			throw new MyException("参数错误：parms不能为空");
		}
		
		Matcher matcher = NAMED_PARM_PATTERN.matcher(sql);
		Set<String> namedSet = new LinkedHashSet<>();
		while (matcher.find()) {
			if (namedSet.contains(matcher.group().substring(1))) {
				throw new MyException("命名参数重复");
			}
			namedSet.add(matcher.group().substring(1));
		}
		
		if (namedSet.size() != parms.length) {
			throw new MyException("参数错误：parms长度错误");
		}
		
		return new ArrayList<String>(namedSet);
	}
	
	public List<Object> getFromParmList() {
		return fromParmList;
	}
	
	public List<Object> getWhereParmList() {
		return whereParmList;
	}
	
	public String getWhere() {
		return where.toString();
	}
	
	@Override
	public String toString() {
		return getSql();
	}
	
	/**
	 * 排序枚举，用于获取asc或desc字符串。
	 * 
	 * @author zhanghc 2015-3-30下午03:46:14
	 */
	public enum Order {
		ASC("ASC"), DESC("DESC"), NULL("");

		private String value;

		private Order(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}
}
