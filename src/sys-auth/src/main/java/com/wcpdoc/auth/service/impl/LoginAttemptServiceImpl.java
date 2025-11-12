package com.wcpdoc.auth.service.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.wcpdoc.auth.service.LoginAttemptService;
import com.wcpdoc.base.constant.BaseConstant;

import lombok.RequiredArgsConstructor;

/**
 * 登录尝试服务层实现
 * 
 * v1.0 zhanghc 2025年11月4日下午8:10:11
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {
	private static final int MAX_ATTEMPTS = 5;
	private final CacheManager cacheManager;

	@Override
	public void fail(String loginName) {
		Cache cache = cacheManager.getCache(BaseConstant.LOGIN_ATTEMPT_CACHE);
		Integer current = cache.get(loginName, Integer.class);
		int attempts = (current != null ? current : 0) + 1;
		cache.put(loginName, attempts);
	}

	@Override
	public boolean isLock(String loginName) {
		Cache cache = cacheManager.getCache(BaseConstant.LOGIN_ATTEMPT_CACHE);
		Integer attempts = cache.get(loginName, Integer.class);
		return attempts != null && attempts >= MAX_ATTEMPTS;
	}

	@Override
	public void succ(String loginName) {
		Cache cache = cacheManager.getCache(BaseConstant.LOGIN_ATTEMPT_CACHE);
		cache.evict(loginName);
	}
}