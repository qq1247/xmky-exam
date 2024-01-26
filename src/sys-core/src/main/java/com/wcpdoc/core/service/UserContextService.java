package com.wcpdoc.core.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wcpdoc.core.entity.LoginUser;

/**
 * 用户上下文服务层接口
 * 
 * v1.0 zhanghc 2021年10月15日下午1:19:49
 */
public interface UserContextService {

	/**
	 * 获取用户信息
	 * 
	 * v1.0 zhanghc 2021年10月15日下午1:19:49
	 * 
	 * @param request
	 * @param response
	 * @param handler
	 * @return LoginUser
	 */
	LoginUser getUser(HttpServletRequest request, HttpServletResponse response, Object handler);

	/**
	 * 校验用户有效性
	 * 
	 * v1.0 zhanghc 2022年1月12日上午10:37:00
	 * 
	 * @param user
	 * @return boolean
	 */
	boolean valide(LoginUser user);

}
