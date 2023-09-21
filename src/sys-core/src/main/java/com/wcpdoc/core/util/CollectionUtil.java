package com.wcpdoc.core.util;

import java.util.LinkedHashSet;
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
	 * @param objs
	 * @return Set<T>
	 */
	public static <T> Set<T> toSet(T[] objs) {
		Set<T> set = new LinkedHashSet<>(objs.length);
		if (objs == null || objs.length == 0) {
			return set;
		}

		for (T obj : objs) {
			set.add(obj);
		}
		return set;
	}
}
