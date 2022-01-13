package com.wcpdoc.auth.cache;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;

import com.wcpdoc.cache.BaseEhCache;

/**
 * jwtSecret缓存
 * 
 * v1.0 chenyun 2021年11月12日下午1:25:42
 */
public class JwtSecretCache extends BaseEhCache {
	private static final Logger log = LoggerFactory.getLogger(JwtSecretCache.class);
	private static final String CACHE_NAME = "MY_CACHE";
	private static final String JWT_SECRET = "JWT_SECRET";

	static {
		initCache();
	}

	/**
	 * 初始化缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:36 void
	 */
	private static void initCache() {
		log.info("缓存：jwtSecret初始化开始");
		Cache cache = getCache(CACHE_NAME);
		cache.put(JWT_SECRET, new HashMap<String, String>());
		log.info("缓存：jwtSecret初始化成功");
	}

	/**
	 * 添加缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:17 void
	 */
	public static void addCache(String value) {
		getValueMap().clear();
		Map<String, String> jwtSecretMap = getValueMap();
		jwtSecretMap.put("jwtSecret", value);
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:25:10
	 * @return Map<String,String>
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, String> getValueMap() {
		Cache cache = getCache(CACHE_NAME);
		return cache.get(JWT_SECRET, Map.class);
	}
	
	/**
	 * 获取缓存
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:24:54
	 * @return String
	 */
	private static String getValue() {
		return getValueMap().get("jwtSecret");
	}
	
	/**
	 * 获取缓存值
	 * 
	 * v1.0 chenyun 2021年11月12日下午1:24:21
	 * @return String
	 */
	public static String get() {
		String value = getValue();
		return value == null ? null : value;
	}
}
