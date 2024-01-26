package com.wcpdoc.core.exception;

/**
 * 自定义异常
 * 
 * v1.0 zhanghc 2019年11月4日下午6:45:38
 */
public class MyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MyException(String msg) {
		super(msg);
	}

	public MyException(Throwable cause) {
		super(cause);
	}
}
