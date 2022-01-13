package com.wcpdoc.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wcpdoc.core.context.UserContext;
import com.wcpdoc.core.entity.LoginUser;

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
		return UserContext.get();
	}
}
