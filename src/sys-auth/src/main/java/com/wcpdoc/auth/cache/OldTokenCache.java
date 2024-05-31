package com.wcpdoc.auth.cache;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;

/**
 * 旧令牌缓存
 * 
 * v1.0 zhanghc 2021年3月18日下午3:30:37
 */
public class OldTokenCache extends BaseEhCache {
	private static final String CACHE_NAME = "OLD_TOKEN_CACHE";

	/**
	 * 放入缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param key
	 * @param value void
	 */
	public static void put(String key, String value) {
		Cache cache = getCache(CACHE_NAME);
		cache.put(key, value);
//		net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
//		nativeCache.flush(); //spring包装后，实现了flush
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
	 * @param key
	 * @param value void
	 */
	public static String get(String key) {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(key, String.class);
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
}
