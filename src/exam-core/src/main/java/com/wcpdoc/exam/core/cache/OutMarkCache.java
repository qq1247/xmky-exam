package com.wcpdoc.exam.core.cache;

import java.util.Date;
import java.util.List;

import org.springframework.cache.Cache;

import com.wcpdoc.cache.BaseEhCache;

/**
 * 完成阅卷缓存
 * 
 * v1.0 chenyun 2021年11月30日 15:12:01
 */
public class OutMarkCache extends BaseEhCache {
	private static final String CACHE_NAME = "OUT_MARK_CACHE";

	/**
	 * 放入缓存
	 * 
	 * v1.0 chenyun 2021年11月30日 15:12:01
	 * @param key
	 * @param value 
	 * void
	 */
	public static void put(Integer key, Date value) {
		Cache cache = getCache(CACHE_NAME);
		cache.put(key, value);
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月30日 15:12:01
	 * @param key
	 * @param value 
	 * void
	 */
	public static Date get(Integer key) {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(key, Date.class);
	}
	
	/**
	 * 删除缓存
	 * 
	 * v1.0 chenyun 2021年11月30日 15:12:01
	 * @param key
	 * @param value 
	 * void
	 */
	public static void del(Integer key) {
		Cache cache = getCache(CACHE_NAME);
		cache.evict(key);
	}
	
	/**
	 * 缓存列表
	 * 
	 * v1.0 chenyun 2021年11月30日 15:12:01
	 * @param key
	 * @param value 
	 * void
	 */
	@SuppressWarnings("unchecked")
	public static List<Integer> getList() {
		Cache cache = getCache(CACHE_NAME);
		net.sf.ehcache.Cache nativeCache = (net.sf.ehcache.Cache) cache.getNativeCache();
		return nativeCache.getKeys();
	}
}
