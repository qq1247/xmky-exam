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

		// 公共请求
		Map<String, String> filterChainMap = new LinkedHashMap<>();
		filterChainMap.put("/api/login/*", "anon");// 登录相关功能免登录
		filterChainMap.put("/api/file/download", "anon");// 下载免登录
		filterChainMap.put("/api/dict/listpage", "jwt");// 数据字典分页列表需登录
		filterChainMap.put("/api/bulletin/listpage", "jwt");// 公告分页列表需登录
		filterChainMap.put("/api/bulletin/get", "jwt");// 公告详情列表需登录
		filterChainMap.put("/api/user/get", "jwt");// 用户信息需登录
		filterChainMap.put("/api/progressBar/*", "jwt");// 用户信息需登录

		// 子管理员权限
		filterChainMap.put("/api/user/listpage", "jwt,anyRolesEx[0,2]");// 用户添加
		filterChainMap.put("/api/user/add", "jwt,anyRolesEx[0,2]");// 用户添加
		filterChainMap.put("/api/user/edit", "jwt,anyRolesEx[0,2]");// 用户修改
		filterChainMap.put("/api/user/del", "jwt,anyRolesEx[0,2]");// 用户删除
		filterChainMap.put("/api/user/pwdInit", "jwt,anyRolesEx[0,2]");// 用户密码初始化
		filterChainMap.put("/api/user/frozen", "jwt,anyRolesEx[0,2]");// 用户冻结
		filterChainMap.put("/api/questionType/*", "jwt,anyRolesEx[0,2]");// 题库
		filterChainMap.put("/api/question/*", "jwt,anyRolesEx[0,2]");// 题库
		filterChainMap.put("/api/exer/*", "jwt,anyRolesEx[0,2]");// 练习
		filterChainMap.put("/api/exam/get", "jwt,anyRolesEx[0,2,3]");// 考试详情
		filterChainMap.put("/api/exam/*", "jwt,anyRolesEx[0,2]");// 考试
		filterChainMap.put("/api/myMark/*", "jwt,anyRolesEx[0,2,3]");// 我的阅卷（管理员、子管理员、阅卷用户都有权限，只是数据权限不一样）
		filterChainMap.put("/api/report/subAdmin/home", "jwt,anyRolesEx[2]");// 考试
		filterChainMap.put("/api/report/exam/rankListpage", "jwt,anyRolesEx[0,2]");// 报表相关
		filterChainMap.put("/api/report/exam/statis", "jwt,anyRolesEx[0,2]");// 报表相关

		// 考试用户权限
		filterChainMap.put("/api/myExam/*", "jwt,anyRolesEx[1]");// 我的考试
		filterChainMap.put("/api/myExer/*", "jwt,anyRolesEx[1]");// 我的练习
		filterChainMap.put("/api/report/user/home", "jwt,anyRolesEx[1]");// 用户首页

		// 阅卷用户权限
		filterChainMap.put("/api/report/markUser/home", "jwt,anyRolesEx[3]");// 阅卷用户首页

		// 管理员权限
		filterChainMap.put("/api/**", "jwt,anyRolesEx[0]");// 剩余都是
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
