package com.wcpdoc.exam.core.entity;

/**
 * 登录用户
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public interface LoginUser {

	/**
	 * 获取用户ID
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @return Integer
	 */
	public Integer getId();

	/**
	 * 获取用户昵称
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @return String
	 */
	public String getName();
	
	/**
	 * 获取登录名称
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * @return String
	 */
	public String getLoginName();
}
