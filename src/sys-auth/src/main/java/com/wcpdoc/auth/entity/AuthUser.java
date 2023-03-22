package com.wcpdoc.auth.entity;

/**
 * 权限用户
 * 
 * v1.0 zhanghc 2021年3月19日下午4:49:54
 */
public interface AuthUser {

	/**
	 * 获取用户ID
	 * 
	 * v1.0 zhanghc 2021年3月19日下午4:49:54
	 * @return Integer
	 */
	public Integer getId();

	/**
	 * 获取用户昵称
	 * 
	 * v1.0 zhanghc 2021年3月19日下午4:49:54
	 * @return String
	 */
	public String getName();
	
	/**
	 * 获取登录账号
	 * 
	 * v1.0 zhanghc 2021年3月19日下午4:49:54
	 * @return String
	 */
	public String getLoginName();
	
	/**
	 * 获取登录角色
	 * 
	 * v1.0 zhanghc 2021年3月19日下午4:49:54
	 * @return String
	 */
	public String[] getRoles();
}
