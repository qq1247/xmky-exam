package com.wcpdoc.auth.entity;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * jwt令牌
 * 
 * v1.0 zhanghc 2021年3月19日下午5:13:51
 */
public class JWTToken implements AuthenticationToken {
	private static final long serialVersionUID = 1L;
	private String jwt;

	public JWTToken(String jwt) {
		this.jwt = jwt;
	}

	@Override
	public Object getPrincipal() {
		return jwt;
	}

	@Override
	public Object getCredentials() {
		return jwt;
	}
}
