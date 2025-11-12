package com.wcpdoc.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 缓存控制层
 * 
 * v1.0 zhanghc 2024年5月30日上午10:24:48
 */
@RestController
@RequestMapping("/api/cache")
@RequiredArgsConstructor
@Slf4j
public class ApiCacheController extends BaseController {
	private final CacheManager cacheManager;

	/**
	 * 缓存列表
	 * 
	 * v1.0 zhanghc 2024年5月30日上午10:26:21
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/list")
	public PageResult list() {
		try {
			Map<Object, Object> data = new HashMap<>();
			for (String cacheName : cacheManager.getCacheNames()) {
				Cache cache = cacheManager.getCache(cacheName);
				Object nativeCache = cache.getNativeCache();

				@SuppressWarnings("unchecked")
				com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache = (com.github.benmanes.caffeine.cache.Cache<Object, Object>) nativeCache;
				caffeineCache.asMap().forEach((key, value) -> {
					String fullKey = cacheName + ":" + key;
					data.put(fullKey, value);
				});
			}

			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("缓存列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("缓存列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 缓存刷新
	 * 
	 * v1.0 zhanghc 2024年5月30日上午9:28:11
	 * 
	 * @param cacheNames
	 * @return PageResult
	 */
	@RequestMapping("/refresh")
	public PageResult refresh(String[] cacheNames) {
		try {
			if (cacheNames == null || cacheNames.length == 0) {
				return PageResult.err().msg("缓存名称不能为空");
			}

			for (String cacheName : cacheNames) {
				Cache cache = cacheManager.getCache(cacheName);
				cache.clear();
				log.info("缓存清理：{}", cacheName);
			}
			return PageResult.ok();
		} catch (MyException e) {
			log.error("缓存刷新错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("缓存刷新错误：", e);
			return PageResult.err();
		}
	}

}
