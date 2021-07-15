package com.wcpdoc.exam.core.util;

import java.util.Collection;

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
}
