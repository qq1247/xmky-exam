package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.User;

/**
 * 用户扩展服务层接口
 * 
 * v1.0 zhanghc 2021年10月15日下午5:58:19
 */
public interface UserExService {

	/**
	 * 修改角色
	 * 
	 * v1.0 zhanghc 2021年10月15日下午5:58:08
	 * @param userId 
	 * void
	 */
	void roleUpdate(Integer userId);

	/**
	 * 生成令牌
	 * 
	 * v1.0 zhanghc 2021年10月18日上午10:16:02
	 * @param user
	 * @return String
	 */
	String generateToken(User user);
	
	/**
	 * 刷新令牌
	 * 
	 * v1.0 zhanghc 2021年10月18日上午10:16:02
	 * @param user
	 * @param token
	 * void
	 */
	void refreshToken(User user, String token);

}
