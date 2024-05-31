package com.wcpdoc.auth.entity;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 令牌工具类
 * 
 * v1.0 zhanghc 2019年10月17日下午5:11:12
 */

@Data
@AllArgsConstructor
public class JwtResult {
	protected Integer code;
	protected String msg;
	protected Claims claims;
}
