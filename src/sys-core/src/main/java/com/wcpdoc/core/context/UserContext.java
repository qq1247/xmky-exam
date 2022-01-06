package com.wcpdoc.core.context;

import com.wcpdoc.core.entity.LoginUser;

/**
 * 用户容器
 * 
 * v1.0 zhanghc 2021年10月8日下午5:16:38
 */
public class UserContext {
	private static final ThreadLocal<LoginUser> context = new ThreadLocal<>();

	public static void set(LoginUser loginuser) {
		context.set(loginuser);
	}

	public static LoginUser get() {
		return context.get();
	}

	public static void remove() {
		context.remove();
	}
}
