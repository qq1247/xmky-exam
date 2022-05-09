package com.wcpdoc.base.entity;

/**
 * 用户令牌
 * 
 * v1.0 zhanghc 2017年7月17日下午4:20:21
 */
public class UserToken {
	private String accessToken;
	private Integer userId;
	private String userName;
	private String[] roles;
	private String entName;
	private Integer entLogoFileId;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String[] getRoles() {
		return roles;
	}

	public void setRoles(String[] roles) {
		this.roles = roles;
	}

	public String getEntName() {
		return entName;
	}

	public void setEntName(String entName) {
		this.entName = entName;
	}

	public Integer getEntLogoFileId() {
		return entLogoFileId;
	}

	public void setEntLogoFileId(Integer entLogoFileId) {
		this.entLogoFileId = entLogoFileId;
	}

}
