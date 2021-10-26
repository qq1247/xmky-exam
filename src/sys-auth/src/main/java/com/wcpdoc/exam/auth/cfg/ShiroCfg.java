package com.wcpdoc.exam.auth.cfg;

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

import com.wcpdoc.exam.auth.filter.AnyRolesEx;
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
		Map<String, Filter> jwtFilterMap = new HashMap<>(2);
		jwtFilterMap.put("anyRolesEx", new AnyRolesEx());
		jwtFilterMap.put("jwt", new JWTFilter());
		shiroFilterFactory.setFilters(jwtFilterMap);

		Map<String, String> filterChainMap = new LinkedHashMap<>();
		filterChainMap.put("/api/file/download", "anon");
		filterChainMap.put("/api/login/**", "anon");// /api/login开头的可以匿名访问
		
		//管理员请求
		filterChainMap.put("/api/dict/add", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/dict/edit", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/dict/del", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/dict/get", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/cron/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/parm/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/parm/**", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/add", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/edit", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/del", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/move", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/input", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/export", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/template", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/org/get", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/add", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/edit", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/del", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/roleUpdate", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/initPwd", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/input", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/export", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/template", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/get", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/onList", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/exit", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/user/syncUser", "jwt,anyRolesEx[admin]");
		filterChainMap.put("/api/sensitive/**", "jwt,anyRolesEx[admin]");
		
		//子管理请求
		filterChainMap.put("/api/bulletin/add", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/bulletin/edit", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/bulletin/del", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/bulletin/auth", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/bulletin/authUserList", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/exam/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/examType/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/listpage", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/add", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/edit", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/del", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/copy", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/archive", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/chapterAdd", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/chapterEdit", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/chapterDel", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/movePosition", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/questionAdd", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/updateScore", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/updateScoreOptions", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/updateBatchScore", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/questionDel", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/questionClear", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/publish", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paper/updateTotalScore", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paperRemark/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/paperType/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/question/**", "jwt,anyRolesEx[subAdmin]");
		filterChainMap.put("/api/questionType/**", "jwt,anyRolesEx[subAdmin]");
		
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
