package com.wcpdoc.exam.home.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.service.BaseService;
/**
 * 首页服务层接口
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
public interface HomeService extends BaseService<Object>{

	/**
	 * 获取权限总和
	 * 
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @param userId
	 * @return Map<Integer,Long>
	 */
	Map<Integer, Long> getAuthSum(Integer userId);

	/**
	 * 完成登录
	 * 
	 * v1.0 zhanghc 2017年8月3日上午10:55:36
	 * @param loginName
	 * @param pwd
	 * @param request
	 * @return User
	 */
	User doIn(String loginName, String pwd, HttpServletRequest request);

	/**
	 * 完成退出
	 * 
	 * v1.0 zhanghc 2017年8月3日上午11:20:04
	 * @param request
	 * void
	 */
	void doOut(HttpServletRequest request);

}
