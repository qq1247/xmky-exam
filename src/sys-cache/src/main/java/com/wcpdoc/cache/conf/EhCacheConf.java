package com.wcpdoc.cache.conf;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * 缓存配置
 * 
 * v1.0 zhanghc 2019年5月25日上午9:56:48
 */
@Configuration
@EnableCaching
public class EhCacheConf {

	public EhCacheConf() {
		System.setProperty(net.sf.ehcache.CacheManager.ENABLE_SHUTDOWN_HOOK_PROPERTY, "true");// 参数用于关闭程序时回调接口持久化数据，kill无效。
	}

	/**
	 * 自定义配置
	 * 
	 * v1.0 zhanghc 2019年5月25日上午9:56:48
	 * 
	 * @param factoryBean
	 * @return EhCacheCacheManager
	 */
	@Bean
	public EhCacheCacheManager ehCacheCacheManager(EhCacheManagerFactoryBean factoryBean) {
		return new EhCacheCacheManager(factoryBean.getObject());
	}

	@Bean
	public EhCacheManagerFactoryBean cacheManagerFactoryBean() {
		EhCacheManagerFactoryBean factoryBean = new EhCacheManagerFactoryBean();
		factoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
		factoryBean.setShared(true);
		return factoryBean;
	}
}
