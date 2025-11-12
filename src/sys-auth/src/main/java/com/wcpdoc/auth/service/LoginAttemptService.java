package com.wcpdoc.auth.service;

/**
 * 登录尝试服务层接口
 * 
 * v1.0 zhanghc 2025年11月4日下午8:10:11
 */
public interface LoginAttemptService {
	/**
	 * 登录尝试失败（累加失败次数）
	 * 
	 * v1.0 zhanghc 2025年11月4日下午8:11:55
	 * 
	 * @param loginName void
	 */
	void fail(String loginName);

	/**
	 * 登录尝试是否被锁定（失败次数 >= 最大次数）
	 * 
	 * v1.0 zhanghc 2025年11月4日下午8:12:05
	 * 
	 * @param loginName
	 * @return boolean
	 */
	boolean isLock(String loginName);

	/**
	 * 登录尝试成功（清除失败次数）
	 * 
	 * v1.0 zhanghc 2025年11月4日下午8:12:23
	 * 
	 * @param loginName void
	 */
	public void succ(String loginName);
}