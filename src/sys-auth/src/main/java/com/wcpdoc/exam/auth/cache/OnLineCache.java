package com.wcpdoc.exam.auth.cache;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
	public static void setOnLineTime(Integer id, String ip) {
		log.debug("设置在线缓存：{}-{}", id);
		Cache cache = getCache(CACHE_NAME);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("ip", ip);
		map.put("time", new Date().getTime());
		cache.put(id, map);
	}
	
	/**
	 * 获取在线缓存
	 * 
	 * v1.0 chenyun 2021年9月7日上午11:04:56
	 * @param id
	 * @return Long
	 */
	@SuppressWarnings("rawtypes")
	public static Long getOnLineTime(Integer id) {
		Cache cache = getCache(CACHE_NAME);
		Map map = cache.get(id, Map.class);
		log.debug("获取在线缓存：{}-{}", id, Long.getLong(map.get("time").toString()));
		return Long.getLong(map.get("time").toString());
	}
}
