package com.wcpdoc.exam.base.cfg;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.util.JwtUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
		filterChainMap.put("/login/**", "anon");// login开头的可以匿名访问
		filterChainMap.put("/**", "jwt");// 其他请求走jwt过滤器
		shiroFilterFactory.setFilterChainDefinitionMap(filterChainMap);
		return shiroFilterFactory;
	}
	
	/**
	 * jwt过滤器
	 * 
	 * v1.0 zhanghc 2021年3月2日上午9:52:04
	 */
	class JWTFilter extends BasicHttpAuthenticationFilter {
		/**
		 * 是否允许访问
		 */
		@Override
		protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
			if (!isLoginAttempt(request, response)) {
				return true;
			}

			try {
				executeLogin(request, response);
				return true;
			} catch (Exception e) {
				return true;
			}
		}

		/**
		 * 是否想登陆
		 */
		@Override
		protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String jwt = httpRequest.getHeader("Authorization");
			return ValidateUtil.isValid(jwt);
		}

		/**
		 * 执行登陆
		 */
		@Override
		protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String jwt = httpRequest.getHeader("Authorization");
			JWTToken jwtToken = new JWTToken(jwt);
			getSubject(httpRequest, response).login(jwtToken);
			return true;
		}

		/**
		 * 拒绝访问返回信息
		 */
		@Override
		protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
			HttpServletResponse httpResponse = WebUtils.toHttp(response);
			httpResponse.setCharacterEncoding("UTF-8");
			httpResponse.setContentType("application/json;charset=UTF-8");
			httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
			httpResponse.getWriter().write("{\"code\": 401, \"msg\": \"未授权\"}");
			return false;
		}

		@Override
		protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;
			httpResponse.setHeader("Access-control-Allow-Origin", httpRequest.getHeader("Origin"));
			httpResponse.setHeader("Access-control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
			httpResponse.setHeader("Access-control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
			if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
				httpResponse.setStatus(HttpStatus.OK.value());
				return false;
			}
			return super.preHandle(request, response);
		}
	}
	
	/**
	 * jwt授权认证
	 * 
	 * v1.0 zhanghc 2021年3月2日上午9:51:13
	 */
	@Component
	public class JWTRealm extends AuthorizingRealm {
		@Resource
		private UserService userService;

		@Override
		public boolean supports(AuthenticationToken token) {
			return token instanceof JWTToken;
		}

		/**
		 * 授权
		 */
		@Override
		protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
			String jwt = (String) principals.getPrimaryPrincipal();
			JwtResult jwtResult = JwtUtil.parse(jwt);
			String loginName = jwtResult.getClaims().get("loginName", String.class);
			User user = userService.getUser(loginName);
			if (user == null) {
				throw new UnknownAccountException("账号密码错误");
			}

			List<Post> postList = userService.getPostList(user.getId());
			SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
			for (Post post : postList) {
				simpleAuthorizationInfo.addRole(post.getName());
			}
			
			return simpleAuthorizationInfo;
		}

		/**
		 * 认证
		 */
		@Override
		protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
			String jwtToken = (String) token.getPrincipal();
			if (!ValidateUtil.isValid(jwtToken)) {
				throw new UnknownAccountException("token为空");
			}

			JwtResult result = JwtUtil.parse(jwtToken);
			if (!result.isSucc()) {
				throw new UnknownAccountException(result.getMsg());
			}

			return new SimpleAuthenticationInfo(jwtToken, jwtToken, getName());
		}
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
		securityManager.setRealm(jwtRealm);//自定义realm
		securityManager.setCacheManager(ehCacheManager);// 使用ehcache
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
	 * 授权第二次使用缓存查询
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
	 * 注解支持
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
