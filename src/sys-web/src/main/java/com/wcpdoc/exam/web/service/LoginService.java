package com.wcpdoc.exam.web.service;

import javax.servlet.http.HttpServletRequest;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.User;
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
	User doIn(String loginName, String pwd, HttpServletRequest request);
	
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
	 * @param id
	 * @param oldPwd
	 * @param newPwd
	 * void
	 */
	void doPwdUpdate(Integer userId, String oldPwd, String newPwd);
}
