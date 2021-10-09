package com.wcpdoc.exam.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wcpdoc.exam.core.context.UserContext;
import com.wcpdoc.exam.core.entity.JwtResult;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.util.JwtUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 用户容器拦截器
 * 
 * v1.0 zhanghc 2021年10月8日下午5:40:00
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {
	/**
	 * 请求开始前，解析当前登录用户，绑定到当前线程上
	 * 
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String jwt = request.getHeader("Authorization");// 请求头没有token，不处理
		if (!ValidateUtil.isValid(jwt)) {
			return true;
		}
		
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwt);// 令牌解析失败，不处理
		if (jwtResult.getCode() != 200) {
			return true;
		}
		
		UserContext.set(new LoginUser() {
			@Override
			public Integer getId() {
				return jwtResult.getClaims().get("userId", Integer.class);
			}

			@Override
			public String getLoginName() {
				return jwtResult.getClaims().get("loginName", String.class);
			}
		});
		
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
	 * 请求结束后，清理绑定到当前线程上的数据
	 * 
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		UserContext.remove();
	}
}
