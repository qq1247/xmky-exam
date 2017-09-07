package com.wcpdoc.exam.core.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

/**
 * 加密工具类
 * 
 * v1.0 zhanghc 2017年7月13日下午5:32:08
 */
public class EncryptUtil {
	
	/**
	 * 获取字符串md5后base64值
	 * 
	 * v1.0 zhanghc 2017年7月13日下午5:33:43
	 * @param key
	 * @param charset
	 * @return String
	 */
	public static String md52Base64(String key, String charset){
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(key.getBytes(charset));
			return Base64.encodeBase64String(digest);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	/**
	 * 获取字符串md5后base64值
	 * 
	 * v1.0 zhanghc 2017年7月13日下午5:33:43
	 * @param key
	 * @return String
	 */
	public static String md52Base64(String key){
		return md52Base64(key, "utf-8");
	}
	
	public static void main(String[] args) {
		String md52Base64 = md52Base64("sysadmin111111");
		System.err.println(md52Base64);
	}
}
