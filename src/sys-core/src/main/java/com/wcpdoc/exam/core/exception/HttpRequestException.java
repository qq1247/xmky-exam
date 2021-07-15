package com.wcpdoc.exam.core.exception;

/**
 * http请求异常
 * 
 * v1.0 zhanghc 2019年11月4日下午6:45:38
 */
public class HttpRequestException extends Exception {

	private static final long serialVersionUID = 1L;

	public HttpRequestException(String msg) {
		super(msg);
	}
}
