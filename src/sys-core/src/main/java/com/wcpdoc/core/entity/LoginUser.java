package com.wcpdoc.core.entity;

/**
 * 登录用户
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 */
public interface LoginUser {

	/**
	 * 用户ID
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @return Integer
	 */
	public Integer getId();

	/**
	 * 登录账号
	 * 
	 * v1.0 zhanghc 2015-6-19下午08:30:16
	 * 
	 * @return String
	 */
	public String getLoginName();

	/**
	 * 用户类型<br/>
	 * 0：管理员；1：考试用户；2：子管理员；3：阅卷用户<br/>
	 * 
	 * v1.0 zhanghc 2023年9月4日上午10:30:30
	 * 
	 * @return String
	 */
	public Integer getType();
}
