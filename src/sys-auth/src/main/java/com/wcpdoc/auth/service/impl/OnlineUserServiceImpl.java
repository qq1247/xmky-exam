package com.wcpdoc.auth.service.impl;

import java.util.Date;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;

import com.wcpdoc.auth.entity.OnlineUser;
import com.wcpdoc.auth.service.OnlineUserService;
import com.wcpdoc.base.constant.BaseConstant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 在线用户服务层实现
 * 
 * v1.0 zhanghc 2021年10月15日下午2:02:42
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OnlineUserServiceImpl implements OnlineUserService {
	private final CacheManager cacheManager;

	@Override
	public void login(OnlineUser onlineUser) {
		Cache cache = cacheManager.getCache(BaseConstant.ONLINE_USER_CACHE);
		cache.put(onlineUser.getLoginName(), onlineUser);
		log.info("用户【{}】上线", onlineUser.getLoginName());
	}

	@Override
	public void heartbeat(String loginName, String ip) {
		Cache cache = cacheManager.getCache(BaseConstant.ONLINE_USER_CACHE);
		OnlineUser onlineUser = cache.get(loginName, OnlineUser.class);
		if (onlineUser != null) {
			onlineUser.setUpdateTime(new Date());
			onlineUser.setIp(ip);
			cache.put(loginName, onlineUser);
		}
	}

	@Override
	public void logout(String loginName) {
		Cache cache = cacheManager.getCache(BaseConstant.ONLINE_USER_CACHE);
		boolean removed = cache.evictIfPresent(loginName);
		if (removed) {
			log.info("用户【{}】下线", loginName);
		}
	}

	@Override
	public OnlineUser getOnlineUser(String loginName) {
		CaffeineCache cache = (CaffeineCache) cacheManager.getCache(BaseConstant.ONLINE_USER_CACHE);
		return (OnlineUser) cache.getNativeCache().asMap().get(loginName); // 查询时不要触发续期
	}
}