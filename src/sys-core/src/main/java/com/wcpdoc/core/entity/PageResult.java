package com.wcpdoc.core.entity;

import org.springframework.http.HttpStatus;

/**
 * 页面结果
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class PageResult {
	protected static final Integer HTTP_200 = HttpStatus.OK.value();
	protected static final Integer HTTP_500 = HttpStatus.INTERNAL_SERVER_ERROR.value();

	protected static final String HTTP_200_MSG = "请求成功";
	protected static final String HTTP_500_MSG = "未知错误";

	protected Integer code;
	protected String msg;

	protected PageResult() {
	}

	protected PageResult(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static PageResult custom() {
		return new PageResult();
	}

	public static PageResult ok() {
		return new PageResult(HTTP_200, HTTP_200_MSG);
	}

	public static PageResult err() {
		return new PageResult(HTTP_500, HTTP_500_MSG);
	}

	public PageResult code(Integer code) {
		this.code = code;
		return this;
	}

	public PageResult msg(String msg) {
		this.msg = msg;
		return this;
	}

	public Integer getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
