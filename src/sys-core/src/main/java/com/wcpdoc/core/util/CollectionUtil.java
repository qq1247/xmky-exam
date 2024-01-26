package com.wcpdoc.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 集合工具类
 * 
 * v1.0 zhanghc 2023年9月17日上午10:45:56
 */
public class CollectionUtil {

	/**
	 * 转set集合
	 * 
	 * v1.0 zhanghc 2023年9月17日上午10:46:24
	 * 
	 * @param objs
	 * @return Set<T>
	 */
	public static <T> Set<T> toSet(T[] objs) {
		return new HashSet<>(toList(objs));
	}

	/**
	 * 转list集合
	 * 
	 * v1.0 zhanghc 2023年9月17日上午10:46:24
	 * 
	 * @param objs
	 * @return Set<T>
	 */
	public static <T> List<T> toList(T[] objs) {
		if (objs == null || objs.length == 0) {
			return new ArrayList<>(0);
		}

		return Arrays.asList(objs);
	}
}
