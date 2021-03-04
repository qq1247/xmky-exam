package com.wcpdoc.exam.api.entity;

public class PersonToken {
	private String accessToken;
	private String refreshToken;
	private Integer id;
	private Integer saasId;
	
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
	public Integer getSaasId() {
		return saasId;
	}
	public void setSaasId(Integer saasId) {
		this.saasId = saasId;
	}
}
