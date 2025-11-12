package com.wcpdoc.auth.service;

/**
 * 防重放服务层接口
 * 
 * v1.0 zhanghc 2025年11月5日下午6:41:45
 */
public interface NonceService {

	/**
	 * 生成nonce
	 * 
	 * v1.0 zhanghc 2025年11月7日下午8:04:21
	 * 
	 * @param loginName
	 * @return String 格式为 "loginName:uuid" 的 nonce 字符串
	 */
	String generateNonce(String loginName);

	/**
	 * 验证并消费指定的 nonce（一次性使用
	 * 
	 * v1.0 zhanghc 2025年11月7日下午8:05:01
	 * 
	 * @param nonce void
	 */
	void consumeNonce(String nonce);
}