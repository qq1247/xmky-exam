package com.wcpdoc.exam.base.entity;

/**
 * 用户令牌
 * 
 * v1.0 zhanghc 2017年7月17日下午4:20:21
 */
public class UserToken {
	private String accessToken;
	private String refreshToken;
	private Integer id;
	private String loginName;
	private String name;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
