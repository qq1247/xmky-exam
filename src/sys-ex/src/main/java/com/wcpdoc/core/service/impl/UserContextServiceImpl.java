package com.wcpdoc.core.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.wcpdoc.auth.cache.TokenCache;
import com.wcpdoc.auth.entity.JwtResult;
import com.wcpdoc.auth.util.JwtUtil;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.service.UserContextService;
import com.wcpdoc.core.util.ValidateUtil;

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

		JwtResult oldJwtResult = JwtUtil.getInstance().parse(jwt);// 令牌解析失败，不处理
		if (oldJwtResult.getCode() != 200) {
			return null;
		}

		return new LoginUser() {
			@Override
			public Integer getId() {
				return oldJwtResult.getClaims().get("userId", Integer.class);
			}

			@Override
			public String getLoginName() {
				return oldJwtResult.getClaims().get("loginName", String.class);
			}

			@Override
			public Integer getType() {
				return oldJwtResult.getClaims().get("type", Integer.class);
			}
		};
	}

	@Override
	public boolean valide(LoginUser user) {
		return user != null && TokenCache.get(user.getId()) != null;
	}
}
