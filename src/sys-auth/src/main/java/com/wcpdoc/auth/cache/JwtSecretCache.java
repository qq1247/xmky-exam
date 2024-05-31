package com.wcpdoc.auth.cache;

import org.springframework.cache.Cache;

/**
 * jwtSecret缓存
 * 
 * v1.0 chenyun 2021年11月12日下午1:25:42
 */
public class JwtSecretCache extends BaseEhCache {
	private static final String CACHE_NAME = "MY_CACHE";
	private static final String SECRET_KEY = "SECRET_KEY";

	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:24:21
	 * @return String
	 */
	public static String get() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(SECRET_KEY, String.class);
	}

	/**
	 * 刷新缓存
	 * 
	 * v1.0 zhanghc 2022年2月24日下午5:59:54
	 * @param jwtSecret void
	 */
	public static void flushCache(String jwtSecret) {
		Cache cache = getCache(CACHE_NAME);
		cache.put(SECRET_KEY, jwtSecret);
	}
}
