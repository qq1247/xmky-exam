package com.wcpdoc.exam.base.entity;

/**
 * 用户令牌
 * 
 * v1.0 zhanghc 2017年7月17日下午4:20:21
 */
public class UserToken {
	private String accessToken;
	private String name;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
