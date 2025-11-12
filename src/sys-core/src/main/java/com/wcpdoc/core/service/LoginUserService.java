package com.wcpdoc.core.service;

import com.wcpdoc.core.entity.LoginUser;

/**
 * 登录用户服务层接口
 * 
 * v1.0 zhanghc 2021年10月15日下午1:19:49
 */
public interface LoginUserService {

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
	LoginUser getLoginUser();

}
