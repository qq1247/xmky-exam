package com.wcpdoc.exam.auth.filter;

import java.util.Date;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wcpdoc.exam.auth.cache.TokenCache;
import com.wcpdoc.exam.auth.entity.JWTToken;
import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.JwtUtil;
import com.wcpdoc.exam.core.util.SpringUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * jwt过滤器
 * 
 * v1.0 zhanghc 2021年3月2日上午9:52:04
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {
	
	/**
	 * 是否允许访问
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (!isLoginAttempt(request, response)) {
			return false;
		}

		try {
			executeLogin(request, response);
			return true;
		} catch (Exception e) {
			throw new AuthenticationException(e.getMessage());
		}
		
	}

	/**
	 * 是否尝试登陆
	 * 检测http请求header头Authorization字段
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
		String jwt = WebUtils.toHttp(request).getHeader("Authorization");
		JWTToken jwtToken = new JWTToken(jwt);
		getSubject(request, response).login(jwtToken);
		refreshToken(request, response);
		return true;
	}

	/**
	 * 刷新令牌
	 * 
	 * v1.0 zhanghc 2021年3月18日下午4:01:28
	 * @param request
	 * @param response void
	 */
	private void refreshToken(ServletRequest request, ServletResponse response) {
		// 校验数据有效性
		Integer tokenExpireMinute = SpringUtil.getBean(Environment.class).getProperty("token.expireMinute", Integer.class);
		Integer tokenRefreshMinute = SpringUtil.getBean(Environment.class).getProperty("token.refreshMinute", Integer.class);
		String jwtToken = WebUtils.toHttp(request).getHeader("Authorization");
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwtToken);//能到这一步，肯定是登陆成功的，不需要校验空或校验错误。
		long tokenId = Long.parseLong(jwtResult.getClaims().getId());
		int userId = jwtResult.getClaims().get("id", Integer.class);
		String loginName = jwtResult.getClaims().get("loginName", String.class);
		
		Long cacheTokenId = TokenCache.get(String.format("TOKEN_%s", userId));
		if (cacheTokenId == null) {//缓存中没有令牌（过期清理或人工清理）
			throw new AuthenticationException(String.format("用户【%s】令牌不存在", loginName));
		}
		
		if (cacheTokenId != tokenId) {
			throw new AuthenticationException(String.format("用户【%s】令牌过期", loginName));
		}
		
		Date curTime = new Date();
		long tokenTime = tokenId;
		if (curTime.getTime() - tokenTime <= tokenRefreshMinute * 60 * 1000) {// 如果距离上次刷新不超过指定分钟，不处理
			return;
		}
		
		// 生成令牌信息（登陆由shiro接收令牌控制）
		Date expTime = DateUtil.getNextMinute(curTime, tokenExpireMinute);
		String newToken = JwtUtil.getInstance()
			.createToken(curTime.getTime() + "", jwtResult.getClaims().getSubject(), expTime)
			.addAttr("id", jwtResult.getClaims().get("id"))
			.addAttr("loginName", jwtResult.getClaims().get("loginName"))
			.build();
		
		// 缓存刷新令牌（用于续租登陆）
		TokenCache.add(String.format("TOKEN_%s", userId), curTime.getTime());
		
		// 放入http响应头，供前端替换使用
		WebUtils.toHttp(response).setHeader("Authorization", newToken);
	}

	/**
	 * 拒绝访问处理
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

	/**
	 * 支持跨域
	 */
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
		try {
			return super.preHandle(request, response);
		} catch (Exception e) {
			httpResponse.setCharacterEncoding("UTF-8");
			httpResponse.setContentType("application/json;charset=UTF-8");
			httpResponse.setStatus(HttpStatus.OK.value());
			httpResponse.getWriter().write("{\"code\": 401, \"msg\": \""+e.getMessage()+"\"}");
			return false;
		}
	}
	
}
