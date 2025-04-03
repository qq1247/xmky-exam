package com.wcpdoc.auth.cfg;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.Filter;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.wcpdoc.auth.filter.AnyRolesEx;
import com.wcpdoc.auth.filter.DemoModeFilter;
import com.wcpdoc.auth.filter.JWTFilter;
import com.wcpdoc.auth.realm.JWTRealm;

import net.sf.ehcache.CacheManager;

/**
 * 授权认证配置
 * 
 * v1.0 zhanghc 2021年2月25日下午2:22:51
 */
@Configuration
public class ShiroCfg {
	@Resource
	private ShiroFilterCfg shiroFilterCfg;

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
		Map<String, Filter> jwtFilterMap = new LinkedHashMap<>(3);
		jwtFilterMap.put("demoModeFilter", new DemoModeFilter());
		jwtFilterMap.put("jwt", new JWTFilter());
		jwtFilterMap.put("anyRolesEx", new AnyRolesEx());
		shiroFilterFactory.setFilters(jwtFilterMap);

		// 注册权限
		Map<String, String> filterChainMap = new LinkedHashMap<>();
		if (shiroFilterCfg.getDemo().isMode()) {
			filterChainMap.putAll(shiroFilterCfg.getDemo().getUrls().keySet().stream()//
					.map(url -> url.substring(1, url.length() - 1)) // 去掉首尾的[]
					.map(entry -> entry.split("=")) // 按=分割
					.collect(Collectors.toMap(parts -> parts[0], // URL路径作为key
							parts -> parts[1], // 权限规则作为value
							(oldKey, newKey) -> oldKey, 
							LinkedHashMap::new
					)));
		}

		filterChainMap.putAll(shiroFilterCfg.getUrls().keySet().stream()//
				.map(url -> url.substring(1, url.length() - 1)) // 去掉首尾的[]
				.map(entry -> entry.split("=")) // 按=分割
				.filter(parts -> !filterChainMap.containsKey(parts[0])) // 演示模式的key优先
				.collect(Collectors.toMap(parts -> parts[0], // URL路径作为key
						parts -> parts[1], // 权限规则作为value
						(oldKey, newKey) -> oldKey, 
						LinkedHashMap::new
				)));
		shiroFilterFactory.setFilterChainDefinitionMap(filterChainMap);
		return shiroFilterFactory;
	}

	/**
	 * 配置安全管理器
	 * 
	 * v1.0 zhanghc 2021年3月3日下午3:45:07
	 * 
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
	 * 开启注解支持 注解代码分布在各处，侵入代码严重。 改成过滤器字符串匹配，可解耦，可随时替换成其他权限框架。
	 * 
	 * v1.0 zhanghc 2021年3月2日上午11:29:58
	 * 
	 * @param securityManager
	 * @return AuthorizationAttributeSourceAdvisor
	 */
//	@Bean
//	public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
//		AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
//		advisor.setSecurityManager(securityManager);
//		return advisor;
//	}

}
