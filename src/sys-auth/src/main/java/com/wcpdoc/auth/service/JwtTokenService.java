package com.wcpdoc.auth.service;

import java.util.List;

import com.wcpdoc.auth.entity.JwtToken;

/**
 * Jwt令牌服务层接口
 * 
 * v1.0 zhanghc 2025年11月5日下午6:41:45
 */
public interface JwtTokenService {

	/**
	 * 加载秘钥
	 * 
	 * v1.0 zhanghc 2025年11月8日上午1:16:28 void
	 * 
	 * @return
	 */
	void loadSecret();

	/**
	 * 生成访问令牌
	 * 
	 * v1.0 zhanghc 2025年11月3日下午11:03:27
	 * 
	 * @param loginName
	 * @param roles
	 * @param userId
	 * @param userName
	 * @return String
	 */
	String createAccessToken(String loginName, List<String> roles, Integer userId, String userName);

	/**
	 * 生成刷新令牌
	 * 
	 * v1.0 zhanghc 2025年11月3日下午11:03:39
	 * 
	 * @param loginName
	 * @return String
	 */
	String createRefreshToken(String loginName);

	/**
	 * 校验
	 * 
	 * v1.0 zhanghc 2025年11月3日下午11:03:55
	 * 
	 * @param token
	 * @return boolean
	 */
	boolean isValid(String token);

	/**
	 * jwt令牌解析
	 * 
	 * v1.0 zhanghc 2025年11月3日下午11:06:59
	 * 
	 * @param token
	 * @return String
	 */
	JwtToken parse(String token);

	/**
	 * 加入黑名单（用于登出后不能在访问、强制下线等）
	 * 
	 * v1.0 zhanghc 2025年11月3日下午11:03:39
	 * 
	 * @param loginName
	 * @return String
	 */
	void blacklist(String token);

}