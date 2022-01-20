package com.wcpdoc.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 跨域拦截器
 * 
 * v1.0 chenyun 2021年12月27日上午10:51:31
 */
@Component
public class CrossDomainInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(CrossDomainInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		try {
			httpResponse.setHeader("Access-control-Allow-Origin", httpRequest.getHeader("Origin"));
			httpResponse.setHeader("Access-control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
			httpResponse.setHeader("Access-control-Allow-Headers", httpRequest.getHeader("Access-Control-Request-Headers"));
			if (httpRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
				httpResponse.setStatus(HttpStatus.OK.value());
				return false;
			}
		} catch (Exception e) {
			httpResponse.setCharacterEncoding("UTF-8");
			httpResponse.setContentType("application/json;charset=UTF-8");
			httpResponse.setStatus(HttpStatus.OK.value());
			httpResponse.getWriter().write(String.format("{\"code\": %s, \"msg\": \"%s\"}", HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
			log.error("shiro权限异常：", e);
			return false;
		}
		return true;
	}
}
