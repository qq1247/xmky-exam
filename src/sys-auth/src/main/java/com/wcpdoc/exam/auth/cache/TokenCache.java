package com.wcpdoc.exam.auth.cache;

import java.util.ArrayList;
import java.util.List;

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
//		nativeCache.flush(); //spring包装后，实现了flush
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

	/**
	 * 缓存列表
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param key
	 * @param value void
	 */
	@SuppressWarnings("unchecked")
	public static List<Integer> getList() {
		Cache cache = getCache(CACHE_NAME);
		net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
		List<String> keys = nativeCache.getKeys();
		List<Integer> list = new ArrayList<Integer>();
		for(String idString : keys){
			String[] split = idString.split("_");
			list.add(Integer.parseInt(split[1]));
		}
		return list;
	}
	
	/**
	 * 删除缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param key
	 * @param value void
	 */
	public static void del(Integer id) {
		Cache cache = getCache(CACHE_NAME);
		net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
		nativeCache.remove("TOKEN_"+id);
	}
}
