package com.wcpdoc.base.cache;

import com.wcpdoc.cache.BaseEhCache;

/**
 * 用户缓存
 * 
 * v1.0 zhanghc 2022年7月15日下午1:27:34
 */
public class UserCache extends BaseEhCache {
	private static final String CACHE_NAME = "MY_CACHE";
	
	/**
	 * 加读锁
	 * 
	 * v1.0 zhanghc 2021年7月14日下午2:17:54
	 * @param key
	 * @param milliSeconds 
	 * @return boolean
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
	 * @param milliSeconds 
	 * @return boolean
	 * @throws InterruptedException 
	 */
	public static boolean tryWriteLock(Object key, long milliSeconds) throws InterruptedException {
		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
		return nativeCache.tryWriteLockOnKey(key, milliSeconds);
	}
	
	/**
	 * 释放读锁
	 * 
	 * v1.0 zhanghc 2021年7月14日下午2:19:59
	 * @param key
	 * @param milliSeconds
	 * @return
	 * @throws InterruptedException boolean
	 */
	public static void releaseReadLock(Object key) {
		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
		nativeCache.releaseReadLockOnKey(key);
	}
	
	/**
	 * 释放写锁
	 * 
	 * v1.0 zhanghc 2021年7月14日下午2:19:59
	 * @param key
	 * @param milliSeconds
	 * @return
	 * @throws InterruptedException boolean
	 */
	public static void releaseWriteLock(Object key) {
		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
		nativeCache.releaseWriteLockOnKey(key);
	}
}
