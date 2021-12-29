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
 * 支持跨域
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
	
	
//	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//	request.setCharacterEncoding("utf-8");
//	response.setContentType("text/html;charset=utf-8");
//	// 跨域设置
//		if (response instanceof HttpServletResponse) {  //Access-control 和 Access-Control 有大小写问题
//		HttpServletResponse httpServletResponse=(HttpServletResponse)response;
//		//通过在响应 header 中设置 ‘*’ 来允许来自所有域的跨域请求访问。
//        httpServletResponse.setHeader("Access-control-Allow-Origin", "*");
//		//通过对 Credentials 参数的设置，就可以保持跨域 Ajax 时的 Cookie
//        //设置了Allow-Credentials，Allow-Origin就不能为*,需要指明具体的url域
//        //httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
//        //请求方式
//        httpServletResponse.setHeader("Access-control-Allow-Methods", "*");
//        //（预检请求）的返回结果（即 Access-Control-Allow-Methods 和Access-Control-Allow-Headers 提供的信息） 可以被缓存多久
//        httpServletResponse.setHeader("Access-control-Max-Age", "86400");
//        //首部字段用于预检请求的响应。其指明了实际请求中允许携带的首部字段
//        httpServletResponse.setHeader("Access-control-Allow-Headers", "*");
//	}
//	return true;
//	}
	
}
