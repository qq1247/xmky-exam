package com.wcpdoc.notify.exception;

/**
 * 通知服务异常
 * 
 * v1.0 zhanghc 2019年10月15日下午15:51:27
 */
public class EmailException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmailException(String msg) {
		super(msg);
	}
}
