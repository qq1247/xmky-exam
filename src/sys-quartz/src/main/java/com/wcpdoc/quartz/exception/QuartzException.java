package com.wcpdoc.quartz.exception;

/**
 * 定时任务异常
 * 
 * v1.0 zhanghc 2019年7月4日下午2:31:17
 */
public class QuartzException extends Exception {

	private static final long serialVersionUID = 1L;

	public QuartzException(String msg) {
		super(msg);
	}
}
