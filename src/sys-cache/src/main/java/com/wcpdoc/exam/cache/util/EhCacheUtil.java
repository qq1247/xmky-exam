package com.wcpdoc.exam.cache.util;

import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Configuration;

import com.wcpdoc.exam.core.util.SpringUtil;

/**
 * 缓存管理
 * 
 * v1.0 zhanghc 2017年7月5日下午4:22:50
 */
@Configuration
@EnableCaching
public class EhCacheUtil {
	protected static Cache getCache(String cacheName) {
		if (cacheName == null || cacheName.isEmpty()) {
			throw new RuntimeException("无法获取参数：cacheName");
		}

		Cache cache = SpringUtil.getBean(EhCacheCacheManager.class).getCache(cacheName);
		if (cache == null) {
			throw new RuntimeException("缓存" + cacheName + "不存在！");
		}

		return cache;
	}
}
