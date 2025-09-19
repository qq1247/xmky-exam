package com.wcpdoc.api.service;

import javax.security.auth.login.LoginException;

import com.wcpdoc.api.entity.UserToken;
import com.wcpdoc.core.service.BaseService;

/**
 * 登陆服务层接口
 * 
 * v1.0 zhanghc 2017-06-22 22:18:46
 */
public interface LoginService extends BaseService<Object> {

	/**
	 * 登录
	 * 
	 * v1.0 zhanghc 2017年7月17日下午4:20:21
	 * 
	 * @param loginName 账号
	 * @param pwd       密码
	 * @return UserToken
	 */
	UserToken in(String loginName, String pwd) throws LoginException;

	/**
	 * 免登录
	 * 
	 * v1.0 zhanghc 2024年10月9日下午3:38:59
	 * 
	 * @param name
	 * @return UserToken
	 */
	UserToken noLogin(String name) throws LoginException;

	/**
	 * 退出登录
	 * 
	 * v1.0 zhanghc 2017年7月17日下午4:25:41 void
	 */
	void out();

	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午4:37:16
	 * 
	 * @param oldPwd
	 * @param newPwd void
	 */
	void pwdUpdate(String oldPwd, String newPwd);

}
