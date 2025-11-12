package com.wcpdoc.auth.service;

import com.wcpdoc.auth.entity.OnlineUser;

/**
 * 在线用户服务层接口
 * 
 * v1.0 zhanghc 2021年10月15日下午2:02:42
 */
public interface OnlineUserService {

	/**
	 * 在线用户登录
	 * 
	 * v1.0 zhanghc 2025年11月4日下午8:46:20
	 * 
	 * @param onlineUser void
	 */
	void login(OnlineUser onlineUser);

	/**
	 * 在线用户心跳
	 * 
	 * v1.0 zhanghc 2025年11月5日下午9:04:26
	 * 
	 * @param ip
	 * @param loginName void
	 */
	void heartbeat(String loginName, String ip);

	/**
	 * 在线用户退出
	 * 
	 * v1.0 zhanghc 2025年11月4日下午8:46:31
	 * 
	 * @param loginName void
	 */
	void logout(String loginName);

	/**
	 * 在线用户获取
	 * 
	 * v1.0 zhanghc 2025年11月4日下午8:46:43
	 * 
	 * @param loginName
	 * @return OnlineUser
	 */
	OnlineUser getOnlineUser(String loginName);
}