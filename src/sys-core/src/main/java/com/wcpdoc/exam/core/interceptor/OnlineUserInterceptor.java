package com.wcpdoc.exam.core.interceptor;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.wcpdoc.exam.core.context.UserContext;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.OnlineUser;
import com.wcpdoc.exam.core.service.OnlineUserService;

/**
 * 在线用户拦截器
 * 
 * v1.0 zhanghc 2021年10月15日下午1:51:17
 */
@Component
public class OnlineUserInterceptor implements HandlerInterceptor {
	@Resource
	private OnlineUserService onlineUserService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		LoginUser loginUser = UserContext.get();
		if (loginUser == null) {
			return false;
		}
		
		OnlineUser onlineUser = onlineUserService.getEntity(loginUser.getId());
		if (onlineUser != null) {
			onlineUser.setIp(request.getRemoteHost());
			onlineUser.setUpdateTime(new Date());
			onlineUser.setLoginName(loginUser.getLoginName());
			onlineUserService.update(onlineUser);
		} else {
			onlineUser = new OnlineUser();
			onlineUser.setIp(request.getRemoteHost());
			onlineUser.setUpdateTime(new Date());
			onlineUser.setId(loginUser.getId());
			onlineUser.setLoginName(loginUser.getLoginName());
			onlineUserService.add(onlineUser);
		}
		return true;
	}
}
