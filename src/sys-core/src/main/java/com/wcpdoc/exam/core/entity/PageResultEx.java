package com.wcpdoc.exam.core.entity;

/**
 * 页面结果扩展
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class PageResultEx extends PageResult {
	private Object data;

	public PageResultEx() {

	}

	public PageResultEx(boolean succ, String msg, Object data) {
		this.succ = succ;
		this.msg = msg;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object curData) {
		this.data = curData;
	}
}
