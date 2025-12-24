package com.wcpdoc.auth.service;

import java.util.List;

import com.wcpdoc.auth.entity.JwtToken;
import com.wcpdoc.auth.filter.TokenInvalidException;

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
	 * jwt令牌解析
	 * 
	 * v1.0 zhanghc 2025年11月3日下午11:06:59
	 * 
	 * @param token
	 * @return String
	 */
	JwtToken parse(String token) throws TokenInvalidException;

	/**
	 * 加入白名单（用于单设备登录、强制下线等）
	 * 
	 * v1.0 zhanghc 2025年11月3日下午11:03:39
	 * 
	 * @param loginName
	 * @param accessToken
	 * @param refreshToken
	 * @return String
	 */
	void whitelistAdd(String loginName, String accessToken, String refreshToken);

	/**
	 * 移除白名单
	 * 
	 * v1.0 zhanghc 2025年12月23日下午7:34:45
	 * 
	 * @param loginName void
	 */
	void whitelistDel(String loginName);

	/**
	 * 是否白名单
	 * 
	 * v1.0 zhanghc 2025年12月23日下午4:19:04
	 * 
	 * @param loginName
	 * @param token
	 * @return boolean
	 */
	boolean hasWhitelist(String loginName, String token);

}