package com.wcpdoc.exam.core.entity;

import java.util.Map;

public class PageResultEx extends PageResult {
	private Map<String, Object> data;

	public PageResultEx(boolean success, String message, Map<String, Object> data) {
		this.success = success;
		this.message = message;
		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
}
