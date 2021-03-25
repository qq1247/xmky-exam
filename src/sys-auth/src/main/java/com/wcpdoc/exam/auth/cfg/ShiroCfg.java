package com.wcpdoc.exam.auth.cfg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wcpdoc.exam.auth.filter.JWTFilter;
import com.wcpdoc.exam.auth.realm.JWTRealm;

import net.sf.ehcache.CacheManager;

/**
 * 授权认证配置
 * 
 * v1.0 zhanghc 2021年2月25日下午2:22:51
 */
@Configuration
public class ShiroCfg {
	/**
	 * 集成jwt过滤器
	 * 
	 * v1.0 zhanghc 2021年2月26日下午3:49:37
	 * 
	 * @param securityManager
	 * @return ShiroFilterFactoryBean
	 */
	
	@Bean
	public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
		// 设置安全管理器
		ShiroFilterFactoryBean shiroFilterFactory = new ShiroFilterFactoryBean();
		shiroFilterFactory.setSecurityManager(securityManager);

		// 注册jwt过滤器
		Map<String, Filter> jwtFilterMap = new HashMap<>(1);
		jwtFilterMap.put("jwt", new JWTFilter());
		shiroFilterFactory.setFilters(jwtFilterMap);

		Map<String, String> filterChainMap = new LinkedHashMap<>();
		filterChainMap.put("/api/login/**", "anon");// /api/login开头的可以匿名访问
		filterChainMap.put("/**", "jwt");// 其他请求走jwt过滤器
		shiroFilterFactory.setFilterChainDefinitionMap(filterChainMap);
		return shiroFilterFactory;
	}
	
	/**
	 * 配置安全管理器
	 * 
	 * v1.0 zhanghc 2021年3月3日下午3:45:07
	 * @param jwtRealm
	 * @param ehCacheManager
	 * @return SecurityManager
	 */
	@Bean
	public SecurityManager securityManager(JWTRealm jwtRealm, EhCacheManager ehCacheManager) {
		DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
		securityManager.setRealm(jwtRealm);
		securityManager.setCacheManager(ehCacheManager);
		return securityManager;
	}

	/**
	 * 禁用session功能（前后端分离，使用jwt）
	 * 
	 * v1.0 zhanghc 2021年2月26日下午4:38:28
	 * 
	 * @return SessionStorageEvaluator
	 */
	@Bean
	public SessionStorageEvaluator sessionStorageEvaluator() {
		DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
		sessionStorageEvaluator.setSessionStorageEnabled(false);
		return sessionStorageEvaluator;
	}

	/**
	 * 开启缓存支持
	 * 
	 * v1.0 zhanghc 2021年3月3日下午1:55:19
	 * 
	 * @param cacheManager
	 * @return EhCacheManager
	 */
	@Bean
	public EhCacheManager ehCacheManager(CacheManager cacheManager) {
		EhCacheManager ehCacheManager = new EhCacheManager();
		ehCacheManager.setCacheManager(cacheManager);
		return ehCacheManager;
	}

	/**
	 * 开启注解支持
	 * 
	 * v1.0 zhanghc 2021年3月2日上午11:29:58
	 * 
	 * @param securityManager
	 * @return AuthorizationAttributeSourceAdvisor
	 */
	@Bean
	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
		advisor.setSecurityManager(securityManager);
		return advisor;
	}

}
