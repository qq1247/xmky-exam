package com.wcpdoc.exam.core.entity;

import java.util.Map;

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
