package com.wcpdoc.exam.base.cfg;

import org.apache.shiro.authc.AuthenticationToken;

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
