package com.wcpdoc.exam.web.service;

import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.service.BaseService;
/**
 * 登陆服务层接口
 * 
 * v1.0 zhanghc 2017-06-22 22:18:46
 */
public interface LoginService extends BaseService<Object>{
	
	/**
	 * 完成登录
	 * 
	 * v1.0 zhanghc 2017年7月17日下午4:20:21
	 * @param loginName
	 * @param pwd
	 * @param request
	 * @return user
	 */
	User doIn(String loginName, String pwd, HttpServletRequest request) throws LoginException;
	
	/**
	 * 完成退出
	 * 
	 * v1.0 zhanghc 2017年7月17日下午4:25:41
	 * @param request
	 * void
	 */
	void doOut(HttpServletRequest request);

	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午4:37:16
	 * @param oldPwd
	 * @param newPwd
	 * void
	 */
	void doPwdUpdate(String oldPwd, String newPwd);
}
