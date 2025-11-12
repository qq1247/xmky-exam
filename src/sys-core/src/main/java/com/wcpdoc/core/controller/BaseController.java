package com.wcpdoc.core.controller;

import com.wcpdoc.core.context.UserContext;
import com.wcpdoc.core.entity.LoginUser;

import lombok.RequiredArgsConstructor;

/**
 * 控制层
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
@RequiredArgsConstructor
public abstract class BaseController {

	/**
	 * 获取当前登录用户
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @return LoginUser
	 */
	public LoginUser getCurUser() {
		return UserContext.get();
	}
}
