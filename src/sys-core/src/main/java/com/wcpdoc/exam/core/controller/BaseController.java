package com.wcpdoc.exam.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.util.JwtUtil;

import io.jsonwebtoken.Claims;

/**
 * 控制层
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public abstract class BaseController {
	@Resource
	protected HttpServletRequest request;
	@Resource
	protected HttpServletResponse response;

	/**
	 * 获取当前登录用户
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @return
	 */
	public LoginUser getCurUser() {
		String jwt = request.getHeader("Authorization");
		JwtResult parse = JwtUtil.getInstance().parse(jwt);
		Claims claims = parse.getClaims();
		LoginUser LoginUser = new LoginUser() {
			@Override
			public String getLoginName() {
				return (String) claims.get("loginName");
			}
			
			@Override
			public Integer getId() {
				return Integer.valueOf(claims.getId());
			}
		};
		return parse == null ? null : LoginUser;
	}
}
