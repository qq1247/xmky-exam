package com.wcpdoc.exam.core.entity;

public class PageResult {
	protected boolean success;
	protected String message;

	public PageResult() {
	}

	public PageResult(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
