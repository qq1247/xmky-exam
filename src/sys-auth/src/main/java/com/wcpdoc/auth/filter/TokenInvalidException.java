package com.wcpdoc.auth.filter;

import org.springframework.security.core.AuthenticationException;

/**
 * 令牌无效异常
 * 
 * v1.0 zhanghc 2025年12月23日下午4:33:33
 */
public class TokenInvalidException extends AuthenticationException {
	private static final long serialVersionUID = 1L;

	public TokenInvalidException(String msg) {
		super(msg);
	}
}