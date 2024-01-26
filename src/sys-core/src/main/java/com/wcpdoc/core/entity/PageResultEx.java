package com.wcpdoc.core.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面结果扩展
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class PageResultEx extends PageResult {
	protected Object data;

	protected PageResultEx() {

	}

	protected PageResultEx(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public PageResultEx data(Object data) {
		this.data = data;
		return this;
	}

	public static PageResultEx custom() {
		return new PageResultEx();
	}

	public static PageResultEx ok() {
		return new PageResultEx(PageResult.HTTP_200, PageResult.HTTP_200_MSG);
	}

	public static PageResultEx err() {
		return new PageResultEx(HTTP_500, HTTP_500_MSG);
	}

	public Object getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	public PageResultEx addAttr(String key, Object value) {
		if (this.data == null) {
			this.data = new HashMap<>();
		}
		((Map<String, Object>) this.data).put(key, value);
		return this;
	}
}
