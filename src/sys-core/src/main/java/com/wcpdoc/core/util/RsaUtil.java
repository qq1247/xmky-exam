package com.wcpdoc.core.util;

import com.wcpdoc.core.exception.MyException;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * 非对称加密工具类
 * 
 * v1.0 zhanghc 2025年9月18日下午10:27:24
 */
public class RsaUtil {
	private static final RSA rsa = new RSA();

	/**
	 * 获取公共秘钥
	 * 
	 * v1.0 zhanghc 2025年9月18日下午10:28:17
	 * 
	 * @return String
	 */
	public static String getPublicKeyBase64() {
		return rsa.getPublicKeyBase64();
	}

	/**
	 * 解密
	 * 
	 * v1.0 zhanghc 2025年9月18日下午10:28:36
	 * 
	 * @param encryptedBase64
	 * @return String
	 */
	public static String decrypt(String encryptedBase64) {
		try {
			return rsa.decryptStr(encryptedBase64, KeyType.PrivateKey);
		} catch (Exception e) {
			throw new MyException(e);
		}
	}
}