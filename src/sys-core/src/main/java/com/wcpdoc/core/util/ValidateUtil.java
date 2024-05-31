package com.wcpdoc.core.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;

/**
 * 校验工具类
 * 
 * @author zhanghc 2015-3-27下午03:55:02
 */
public class ValidateUtil {

	/**
	 * 字符串校验
	 * 
	 * v1.0 zhanghc 2015-3-27下午04:00:53
	 * 
	 * @param str
	 * @return boolean
	 */
	public static boolean isValid(String str) {
		if (str == null || str.trim().isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 是否有效
	 * 
	 * v1.0 zhanghc 2021年10月19日上午10:42:11
	 * 
	 * @param i
	 * @return boolean
	 */
	public static boolean isValid(Integer i) {
		return i != null;
	}

	/**
	 * 是否有效
	 * 
	 * v1.0 zhanghc 2021年10月19日上午10:42:11
	 * 
	 * @param i
	 * @return boolean
	 */
	public static boolean isValid(Long l) {
		return l != null;
	}

	/**
	 * 是否有效
	 * 
	 * v1.0 zhanghc 2021年10月19日上午10:42:11
	 * 
	 * @param i
	 * @return boolean
	 */
	public static boolean isValid(Double d) {
		return d != null;
	}

	/**
	 * 是否有效
	 * 
	 * v1.0 zhanghc 2021年10月19日上午10:42:11
	 * 
	 * @param i
	 * @return boolean
	 */
	public static boolean isValid(Date d) {
		return d != null;
	}

	/**
	 * 是否有效
	 * 
	 * v1.0 zhanghc 2021年10月19日上午10:42:11
	 * 
	 * @param i
	 * @return boolean
	 */
	public static boolean isValid(BigDecimal b) {
		return b != null;
	}

	/**
	 * 集合校验
	 * 
	 * v1.0 zhanghc 2015-3-27下午04:02:20
	 * 
	 * @param collection
	 * @return boolean
	 */
	public static <T> boolean isValid(Collection<T> collection) {
		if (collection == null || collection.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * 数组校验
	 * 
	 * v1.0 zhanghc 2015-3-27下午04:03:10
	 * 
	 * @param arr
	 * @return boolean
	 */
	public static boolean isValid(Object[] arr) {
		if (arr == null || arr.length == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 布尔校验
	 * 
	 * v1.0 zhanghc 2022年11月10日下午4:03:59
	 * 
	 * @param b
	 * @return boolean
	 */
	public static boolean isValid(Boolean b) {
		return b != null;
	}
}
