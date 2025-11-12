package com.wcpdoc.auth.service.impl;

import java.util.UUID;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.service.NonceService;
import com.wcpdoc.base.constant.BaseConstant;
import com.wcpdoc.core.exception.MyException;

import lombok.RequiredArgsConstructor;

/**
 * 防重放服务层实现
 * 
 * v1.0 zhanghc 2025年11月5日下午6:41:45
 */
@Component
@RequiredArgsConstructor
public class NonceServiceImpl implements NonceService {
	private final CacheManager cacheManager;

	@Override
	public String generateNonce(String loginName) {
		String nonce = String.format("%s:%s", loginName, UUID.randomUUID().toString());
		Cache cache = cacheManager.getCache(BaseConstant.NONCE_CACHE);
		cache.put(nonce, System.currentTimeMillis());
		return nonce;
	}

	@Override
	public void consumeNonce(String nonce) {
		Cache cache = cacheManager.getCache(BaseConstant.NONCE_CACHE);
		Long timestamp = cache.get(nonce, Long.class);
		cache.evict(nonce);

		if (timestamp == null) {
			throw new MyException("nonce重复提交");
		}
		if (System.currentTimeMillis() - timestamp > 3_000L) { // 缓存过期机制在高负载GC下停顿会延迟
			throw new MyException("nonce已过期");
		}
	}
}