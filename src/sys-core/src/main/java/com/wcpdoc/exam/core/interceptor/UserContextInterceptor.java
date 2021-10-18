package com.wcpdoc.exam.core.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wcpdoc.exam.core.context.UserContext;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.service.UserContextService;

/**
 * 用户上下文拦截器
 * 
 * v1.0 zhanghc 2021年10月8日下午5:40:00
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {
	@Resource
	private UserContextService userContextService;
	/**
	 * 请求开始前，解析当前登录用户，绑定到当前线程上
	 * 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LoginUser user = userContextService.getUser(request, response, handler);
		UserContext.set(user);
		return true;
	}
	
	/**
	 * 请求完成时，不处理
	 * 
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		
	}

	/**
	 * 请求完成后，清理绑定到当前线程上的数据
	 * 
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		UserContext.remove();
	}
}
