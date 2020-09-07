package com.wcpdoc.exam.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.entity.LoginUser;

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
		HttpSession session = request.getSession(false);
		return session == null ? null : (LoginUser) session.getAttribute(ConstantManager.USER);
	}
}
