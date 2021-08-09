package com.wcpdoc.exam.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.util.JwtUtil;

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
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwt);

		LoginUser LoginUser = new LoginUser() {
			@Override
			public String getLoginName() {
				return jwtResult.getClaims().get("loginName", String.class);
			}
			
			@Override
			public Integer getId() {
				return jwtResult.getClaims().get("userId", Integer.class);
			}
		};
		return jwtResult == null ? null : LoginUser;
	}
}
