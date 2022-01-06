package com.wcpdoc.auth.entity;

import io.jsonwebtoken.Claims;

/**
 * 令牌工具类
 * 
 * v1.0 zhanghc 2019年10月17日下午5:11:12
 */

public class JwtResult {
	protected Integer code;
	protected String msg;
	protected Claims claims;

	public JwtResult(Integer code, String msg, Claims claims) {
		this.code = code;
		this.msg = msg;
		this.claims = claims;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Claims getClaims() {
		return claims;
	}

	public void setClaims(Claims claims) {
		this.claims = claims;
	}
}
