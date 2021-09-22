package com.wcpdoc.exam.auth.cache;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import com.wcpdoc.exam.cache.BaseEhCache;

/**
 * 在线缓存
 * 
 * v1.0 chenyun 2021年9月3日下午4:18:26
 */
public class OnLineCache extends BaseEhCache{
	private static final Logger log = LoggerFactory.getLogger(OnLineCache.class);
	private static final String CACHE_NAME = "ON_LINE_CACHE";
	
	/**
	 * 设置在线缓存
	 * 
	 * v1.0 chenyun 2021年9月7日上午11:00:42
	 * @param id void
	 */
	public static void setOnLineTime(Integer id) {
		log.debug("设置在线缓存：{}-{}", id);
		Cache cache = getCache(CACHE_NAME);
		cache.put(id, new Date().getTime());
	}
	
	/**
	 * 获取在线缓存
	 * 
	 * v1.0 chenyun 2021年9月7日上午11:04:56
	 * @param id
	 * @return Long
	 */
	public static Long getOnLineTime(Integer id) {
		Cache cache = getCache(CACHE_NAME);
		Long time = cache.get(id, Long.class);
		log.debug("获取在线缓存：{}-{}", id, time);
		return time;
	}
}
