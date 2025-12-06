package com.wcpdoc.cache.conf;

import java.time.Duration;
import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 缓存配置
 * 
 * v1.0 zhanghc 2019年5月25日上午9:56:48 <br/>
 * 默认使用ehcache
 * 
 * v2.0 zhanghc 2025-11-02 16:23:00 <br/>
 * 切换为Caffeine（springboot2切换到springboot3）
 */
@Configuration
@EnableCaching
public class CacheConf {
	private static final long MAX_CACHE_SIZE = 100_000;

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();

		cacheManager.setCaches(List.of(//
				// 登录尝试缓存，用于多次登录失败后锁定账号
				caffeineCache("LOGIN_ATTEMPT_CACHE", Duration.ofMinutes(1), true),
				// 在线用户缓存，用于获取在线用户信息
				caffeineCache("ONLINE_USER_CACHE", Duration.ofMinutes(15), false),
				// 令牌黑名单缓存，用于登出和强制下线（保持和刷新令牌的时长一样，比如过期是10分钟，则第11分钟就不在黑名单了，可以再次换取令牌）
				caffeineCache("TOKEN_BLACKLIST_CACHE", Duration.ofHours(12), true),

				// 防重放缓存：写入后3秒过期
				caffeineCache("NONCE_CACHE", Duration.ofSeconds(3), true),

				// 进度条缓存：空闲1分钟后失效
				caffeineCache("PROGRESS_BAR_CACHE", Duration.ofMinutes(1), false),

				// 数据字典缓存：空闲2小时后失效
				caffeineCache("DICT_CACHE", Duration.ofHours(2), false),

				// 用户缓存：空闲2小时后失效
				caffeineCache("USER_CACHE", Duration.ofHours(2), false),

				// 机构缓存：空闲2小时后失效
				caffeineCache("ORG_CACHE", Duration.ofHours(2), false),

				// 参数缓存：空闲2小时后失效
				caffeineCache("PARM_CACHE", Duration.ofHours(2), false),

				// 试题缓存：空闲2小时后失效
				caffeineCache("QUESTION_CACHE", Duration.ofHours(2), false),

				// 考试缓存：空闲2小时后失效
				caffeineCache("EXAM_CACHE", Duration.ofHours(2), false),

				// 我的考试缓存：空闲2小时后失效
				caffeineCache("MYEXAM_CACHE", Duration.ofHours(2), false),

				// 我的试题缓存：空闲2小时后失效
				caffeineCache("MYQUESTION_CACHE", Duration.ofHours(2), false),

				// 防作弊缓存：写入后60秒过期
				caffeineCache("SXE_CACHE", Duration.ofSeconds(60), true),

				// 练习时长缓存：写入后600秒过期
				caffeineCache("EXER_TIME_CACHE", Duration.ofSeconds(600), true),

				// 用户锁定缓存：空闲60秒后失效
				caffeineCache("USER_LOCK_CACHE", Duration.ofSeconds(60), false),
				
				// 我的评论缓存：空闲2小时后失效
				caffeineCache("MYCOMMENT_CACHE", Duration.ofHours(2), false)

		));

		return cacheManager;
	}

	private CaffeineCache caffeineCache(String name, Duration ttl, boolean useWrite) {
		Caffeine<Object, Object> builder = Caffeine.newBuilder().maximumSize(MAX_CACHE_SIZE);
		if (useWrite) {
			builder.expireAfterWrite(ttl);// 从第一次开始，最大ttl
		} else {
			builder.expireAfterAccess(ttl);// 空闲ttl
		}
		return new CaffeineCache(name, builder.build());
	}
}