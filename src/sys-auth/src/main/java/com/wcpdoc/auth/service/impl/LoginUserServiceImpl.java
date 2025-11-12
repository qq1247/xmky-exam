package com.wcpdoc.auth.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.wcpdoc.auth.entity.UserDetailsImpl;
import com.wcpdoc.core.entity.LoginUser;
import com.wcpdoc.core.service.LoginUserService;

/**
 * 登录用户服务层实现
 * 
 * v1.0 zhanghc 2021年10月15日下午1:44:08
 */
@Service
public class LoginUserServiceImpl implements LoginUserService {

	@Override
	public LoginUser getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl userDetailsImpl) {
			return userDetailsImpl.getLoginUser();
		}
		return null;
	}
}
