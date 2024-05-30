package com.wcpdoc.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;

import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Cache;

/**
 * 缓存控制层
 * 
 * v1.0 zhanghc 2024年5月30日上午10:24:48
 */
@RestController
@RequestMapping("/api/cache")
@Slf4j
public class ApiCacheController extends BaseController {
	@Resource
	private EhCacheCacheManager ehCacheCacheManager;

	/**
	 * 缓存列表
	 * 
	 * v1.0 zhanghc 2024年5月30日上午10:26:21
	 * 
	 * @return Map<Object,Object>
	 */
	@RequestMapping("/list")
	public PageResult list() {
		try {
			Map<Object, Object> data = new HashMap<>();
			ehCacheCacheManager.getCacheNames().forEach(cacheName -> {
				Cache cache = (Cache) ehCacheCacheManager.getCache(cacheName).getNativeCache();
				for (Object key : cache.getKeys()) {
					data.put(key, cache.get(key).getObjectValue());
				}
			});
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
	 * @return PageResult
	 */
	@RequestMapping("/refresh")
	public PageResult refresh(String[] cacheNames) {
		try {
			for (String cacheName : cacheNames) {
				ehCacheCacheManager.getCache(cacheName).clear();
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
