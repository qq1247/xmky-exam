package com.wcpdoc.notify.entity;

/**
 * 邮件实体
 * 
 * v1.0 zhanghc 2021年3月4日下午5:52:07
 */
public class Email {
	private String host;
	private String encode;
	private String userName;
	private String pwd;
	private String protocol;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
