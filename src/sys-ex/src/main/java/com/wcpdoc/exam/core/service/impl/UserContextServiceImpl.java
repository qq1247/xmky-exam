package com.wcpdoc.exam.core.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.auth.entity.JwtResult;
import com.wcpdoc.exam.auth.util.JwtUtil;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.service.UserContextService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 用户上下文服务层实现
 * 
 * v1.0 zhanghc 2021年10月15日下午1:44:08
 */
@Service
public class UserContextServiceImpl implements UserContextService {

	@Override
	public LoginUser getUser(HttpServletRequest request, HttpServletResponse response, Object handler) {
		String jwt = request.getHeader("Authorization");// 请求头没有token，不处理
		if (!ValidateUtil.isValid(jwt)) {
			return null;
		}
		
		JwtResult jwtResult = JwtUtil.getInstance().parse(jwt);// 令牌解析失败，不处理
		if (jwtResult.getCode() != 200) {
			return null;
		}
		
		return new LoginUser() {
			@Override
			public Integer getId() {
				return jwtResult.getClaims().get("userId", Integer.class);
			}

			@Override
			public String getLoginName() {
				return jwtResult.getClaims().get("loginName", String.class);
			}
		};
	}
}
