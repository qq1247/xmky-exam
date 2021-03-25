package com.wcpdoc.exam.auth.cache;

import org.springframework.cache.Cache;

import com.wcpdoc.exam.cache.BaseEhCache;

/**
 * 令牌缓存
 * 
 * v1.0 zhanghc 2021年3月18日下午3:30:37
 */
public class TokenCache extends BaseEhCache {
	private static final String CACHE_NAME = "TOKEN_CACHE";

	/**
	 * 添加缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param key
	 * @param value void
	 */
	public static void add(String key, Long value) {
		Cache cache = getCache(CACHE_NAME);
		cache.put(key, value);
//		net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
//		nativeCache.flush();
	}
	
	/**
	 * 添加缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param key
	 * @param value void
	 */
	public static Long get(String key) {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(key, Long.class);
	}

}
