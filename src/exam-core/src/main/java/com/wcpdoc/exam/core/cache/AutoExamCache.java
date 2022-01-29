package com.wcpdoc.exam.core.cache;

import java.util.Date;
import java.util.List;

import org.springframework.cache.Cache;

import com.wcpdoc.cache.BaseEhCache;

/**
 * 自动阅卷缓存
 * 
 * v1.0 zhanghc 2021年10月25日下午3:10:12
 */
public class AutoExamCache extends BaseEhCache {
	private static final String CACHE_NAME = "AUTO_MARK_CACHE";

	/**
	 * 放入缓存
	 * 
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
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
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
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
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
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
	 * v1.0 zhanghc 2021年3月18日下午3:34:11
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
	
	/**
	 * 加读锁
	 * 
	 * v1.0 zhanghc 2021年7月14日下午2:17:54
	 * @param key
	 * @param milliSeconds void
	 * @return 
	 * @throws InterruptedException 
	 */
	public static boolean tryReadLock(Object key, long milliSeconds) throws InterruptedException {
		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
		return nativeCache.tryReadLockOnKey(key, milliSeconds);
	}
	
	/**
	 * 加写锁
	 * 
	 * v1.0 zhanghc 2021年7月14日下午2:17:54
	 * @param key
	 * @param milliSeconds void
	 * @return 
	 * @throws InterruptedException 
	 */
	public static boolean tryWriteLock(Object key, long milliSeconds) throws InterruptedException {
		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
		return nativeCache.tryWriteLockOnKey(key, milliSeconds);
	}
	
	/**
	 * 释放锁
	 * 
	 * v1.0 zhanghc 2021年7月14日下午2:19:59
	 * @param key
	 * @param milliSeconds
	 * @return
	 * @throws InterruptedException boolean
	 */
	public static void releaseLock(Object key) {
		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
		nativeCache.releaseWriteLockOnKey(key);
	}
}
