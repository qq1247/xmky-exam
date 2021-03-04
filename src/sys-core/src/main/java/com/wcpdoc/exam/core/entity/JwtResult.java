package com.wcpdoc.exam.core.entity;

import io.jsonwebtoken.Claims;

/**
 * 令牌工具类
 * 
 * v1.0 zhanghc 2019年10月17日下午5:11:12
 */

public class JwtResult {
	protected boolean succ;
	protected String msg;
	protected Claims claims;

	public JwtResult(boolean succ, String msg, Claims claims) {
		this.succ = succ;
		this.msg = msg;
		this.claims = claims;
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

	public Claims getClaims() {
		return claims;
	}

	public void setClaims(Claims claims) {
		this.claims = claims;
	}
}
