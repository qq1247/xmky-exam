package com.wcpdoc.exam.core.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.entity.LoginUser;

public abstract class BaseController {
	@Resource
	protected HttpServletRequest request;
	@Resource
	protected HttpServletResponse response;
	
	public LoginUser getCurrentUser(){
		HttpSession session = request.getSession(false);
		if(session == null){
			return null;
		}
		
		return (LoginUser) session.getAttribute(ConstantManager.USER);
	}
}
