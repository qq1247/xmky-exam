package com.wcpdoc.exam.core.entity;

/**
 * 页面结果
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public class PageResult {
	protected boolean succ;
	protected String msg;

	public PageResult() {
	}

	public PageResult(boolean succ, String msg) {
		this.succ = succ;
		this.msg = msg;
	}

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
