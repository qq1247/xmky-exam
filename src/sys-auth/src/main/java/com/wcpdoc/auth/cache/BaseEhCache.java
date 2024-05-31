package com.wcpdoc.auth.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.SpringUtil;

/**
 * 基础缓冲
 * 
 * v1.0 zhanghc 2017年7月5日下午4:22:50
 */
public class BaseEhCache {
	protected static Cache getCache(String cacheName) {
		if (cacheName == null || cacheName.isEmpty()) {
			throw new MyException("参数错误：cacheName");
		}

		Cache cache = SpringUtil.getBean(EhCacheCacheManager.class).getCache(cacheName);
		if (cache == null) {
			throw new MyException("缓存【" + cacheName + "】不存在");
		}

		return cache;
	}

	protected static net.sf.ehcache.Cache getNativeCache(String cacheName) {
		return (net.sf.ehcache.Cache) getCache(cacheName).getNativeCache();
	}
}
