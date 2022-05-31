package com.wcpdoc.auth.cfg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
		Map<String, Filter> jwtFilterMap = new HashMap<>(2);
		jwtFilterMap.put("anyRolesEx", new AnyRolesEx());
		jwtFilterMap.put("jwt", new JWTFilter());
		shiroFilterFactory.setFilters(jwtFilterMap);

		// 匿名请求
		Map<String, String> filterChainMap = new LinkedHashMap<>();
		filterChainMap.put("/api/file/download", "anon");
		filterChainMap.put("/api/file/getId", "anon");
		filterChainMap.put("/api/login/**", "anon");
		
		// 公用请求
		filterChainMap.put("/api/dict/listpage", "jwt,anyRolesEx[admin,subAdmin,user]");
		filterChainMap.put("/api/user/listpage", "jwt,anyRolesEx[admin,subAdmin]");
		filterChainMap.put("/api/org/listpage", "jwt,anyRolesEx[admin,subAdmin]");
		filterChainMap.put("/api/bulletin/listpage", "jwt,anyRolesEx[admin,subAdmin,user]");
		filterChainMap.put("/api/paper/get", "jwt,anyRolesEx[subAdmin,user]");
		filterChainMap.put("/api/exam/get", "jwt,anyRolesEx[subAdmin,user]");
		filterChainMap.put("/api/paper/detail", "jwt,anyRolesEx[subAdmin,user]");
		filterChainMap.put("/api/paper/detailOfRandom", "jwt,anyRolesEx[subAdmin,user]");
		filterChainMap.put("/api/report/home/user", "jwt,anyRolesEx[user]");
		
		//管理员请求
		filterChainMap.put("/api/report/home/admin", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/report/server/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/dict/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/cron/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/parm/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/sensitive/**", "jwt,anyRolesEx[admin]");
		
		//子管理请求
		filterChainMap.put("/api/report/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/exam/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/examType/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/myMark/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paperRemark/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paperType/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/question/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/questionType/**", "jwt,anyRolesEx[subAdmin]");
		
		
		filterChainMap.put("/api/**", "jwt");
		
		// 静态资源
		filterChainMap.put("/**", "anon");
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
	 * 注解代码分布在各处，侵入代码严重。
	 * 改成过滤器字符串匹配，可解耦，可随时替换成其他权限框架。
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
