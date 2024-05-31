package com.wcpdoc.core.cache;

/**
 * 在线用户缓存
 * 
 * v1.0 chenyun 2021年9月3日下午4:18:26
 */
public class OnlineUserCache {
//	private static final String CACHE_NAME = "ONLINE_CACHE";
//
//	/**
//	 * 添加在线用户
//	 * 
//	 * v1.0 chenyun 2021年9月7日上午11:00:42
//	 * 
//	 * @param userId
//	 * @param ip     void
//	 */
//	public static void put(OnlineUser onlineUser) {
//		Cache cache = getCache(CACHE_NAME);
//		cache.put(onlineUser.getId(), onlineUser);
//	}
//
//	/**
//	 * 删除在线用户
//	 * 
//	 * v1.0 zhanghc 2021年10月15日下午4:30:38
//	 * 
//	 * @param onlineUser void
//	 */
//	public static void remove(Integer userId) {
//		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
//		nativeCache.remove(userId);
//	}
//
//	/**
//	 * 获取缓存key
//	 * 
//	 * v1.0 chenyun 2021-12-13 11:04:27
//	 * 
//	 * @param onlineUser void
//	 */
//	@SuppressWarnings("unchecked")
//	public static List<Integer> getKeys() {
//		net.sf.ehcache.Cache nativeCache = getNativeCache(CACHE_NAME);
//		List<Integer> keys = nativeCache.getKeys();
//		return keys;
//	}
//
//	/**
//	 * 获取在线用户
//	 * 
//	 * v1.0 chenyun 2021年9月7日上午11:04:56
//	 * 
//	 * @param userId
//	 * @return OnlineUser
//	 */
//	public static OnlineUser get(Integer userId) {
//		Cache cache = getCache(CACHE_NAME);
//		return cache.get(userId, OnlineUser.class);
//	}

}
