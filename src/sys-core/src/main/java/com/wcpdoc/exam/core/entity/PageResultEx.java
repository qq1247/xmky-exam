package com.wcpdoc.exam.core.entity;

import java.util.Map;

/**
 * 页面结果扩展
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class PageResultEx extends PageResult {
	private Map<String, Object> data;

	public PageResultEx(boolean succ, String msg, Map<String, Object> data) {
		this.succ = succ;
		this.msg = msg;
		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
