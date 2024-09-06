package com.wcpdoc.exam.core.exception;

/**
 * 批量阅卷异常
 * 
 * v1.0 zhanghc 2024年9月5日下午1:47:35
 */
public class BatchMarkException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BatchMarkException(String msg) {
		super(msg);
	}

	public BatchMarkException(Throwable cause) {
		super(cause);
	}
}