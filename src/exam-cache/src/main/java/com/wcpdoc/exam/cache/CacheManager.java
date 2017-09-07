package com.wcpdoc.exam.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.web.context.ContextLoader;

import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 缓存管理
 * 
 * v1.0 zhanghc 2017年7月5日下午4:22:50
 */
public abstract class CacheManager{
	private static final EhCacheCacheManager cacheManager = ContextLoader.getCurrentWebApplicationContext().getBean(EhCacheCacheManager.class);

	protected static Cache getCache(String cacheName) {
		if(!ValidateUtil.isValid(cacheName)){
			throw new RuntimeException("无法获取参数：cacheName");
		}
		
		Cache cache = cacheManager.getCache(cacheName);
		if(cache == null){
			throw new RuntimeException("缓存" + cacheName + "不存在！");
		}
		
		return cache;
	}
}
