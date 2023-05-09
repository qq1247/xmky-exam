package com.wcpdoc.core.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import com.wcpdoc.core.util.ValidateUtil;

/**
 * 页面输入
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class PageIn {
	private int curPage = 1;// 当前第几页，默认第一页
	private int pageSize = 20;// 每页多少条，默认20条，最大100条；

	private ServletRequest request;
	private Map<String, Object> params;
	private List<String> where = new ArrayList<String>();
	private List<Object> whereParmList = new ArrayList<>();
	
	public PageIn() {
	}

	public PageIn(ServletRequest request) {
		this.curPage = !ValidateUtil.isValid(request.getParameter("curPage")) ? this.curPage : Integer.parseInt(request.getParameter("curPage"));
		this.pageSize = !ValidateUtil.isValid(request.getParameter("pageSize")) ? this.pageSize : Integer.parseInt(request.getParameter("pageSize"));
		this.request = request;
	}

	public PageIn addAttr(String key, Object value) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		params.put(key, value);
		return this;
	}

	public PageIn addWhere(String condition) {
		where.add(condition);
		return this;
	}

	public PageIn addWhere(String condition, Object... parms) {
		// 添加where条件
		addWhere(condition);
		
		// 添加where参数
		Collections.addAll(whereParmList, parms);
		return this;
	}

	public PageIn addWhere(boolean result, String condition, Object... parms) {
		if (result) {
			addWhere(condition, parms);
		}
		return this;
	}
	
	public String get(String key) {
		return get(key, String.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> t) {
		if (params != null && params.get(key) != null) {
			return (T) params.get(key);
		}
		if (request == null) {// 非前端调用时，request会为空
			return null;
		}
		return (T) request.getParameter(key);
	}

	public int getCurPage() {
		return curPage;
	}

	public PageIn setCurPage(int curPage) {
		this.curPage = curPage;
		return this;
	}

	public int getPageSize() {
		return pageSize;
	}

	public PageIn setPageSize(int pageSize) {
		this.pageSize = pageSize > 100 ? 100 : pageSize;
		return this;
	}

	public List<Object> getWhereParmList() {
		return whereParmList;
	}
	
	public List<String> getWhere() {
		return where;
	}
}
