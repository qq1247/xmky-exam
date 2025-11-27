package com.wcpdoc.core.util;

import java.security.MessageDigest;
import java.util.Base64;

import com.wcpdoc.core.exception.MyException;

/**
 * 加密工具类
 * 
 * v1.0 zhanghc 2017年7月13日下午5:32:08
 */
public class EncryptUtil {

	/**
	 * md5加密
	 * 
	 * v1.0 zhanghc 2017年7月13日下午5:33:43
	 * 
	 * @param key
	 * @param charset
	 * @return String
	 */
	public static String md52Base64(String key, String charset) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(key.getBytes(charset));
			return Base64.getEncoder().encodeToString(digest);
		} catch (Exception e) {
			throw new MyException(e);
		}
	}

	/**
	 * md5加密
	 * 
	 * v1.0 zhanghc 2017年7月13日下午5:33:43
	 * 
	 * @param key
	 * @return String
	 */
	public static String md52Base64(String key) {
		return md52Base64(key, "utf-8");
	}

	/**
	 * md5加密
	 * 
	 * v1.0 zhanghc 2020年3月28日下午5:03:56
	 * 
	 * @param key
	 * @param charset
	 * @return String
	 */
	public static String md52Hex(String key, String charset) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(key.getBytes(charset));
			StringBuilder sb = new StringBuilder();
			for (byte b : digest) {
				sb.append(String.format("%02x", b & 0xFF));
			}
			return sb.toString();
		} catch (Exception e) {
			throw new MyException(e);
		}
	}

	/**
	 * md5加密
	 * 
	 * v1.0 zhanghc 2020年3月28日下午5:03:56
	 * 
	 * @param key
	 * @param charset
	 * @return String
	 */
	public static String md52Hex(String key) {
		return md52Hex(key, "utf-8");
	}

	public static void main(String[] args) {
		String test1001111111 = EncryptUtil.md52Base64("abc111111");
		System.out.println(test1001111111);
	}

}
