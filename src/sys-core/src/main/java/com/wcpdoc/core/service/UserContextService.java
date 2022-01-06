package com.wcpdoc.core.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wcpdoc.core.entity.LoginUser;

/**
 * 用户上下文服务层接口
 * 
 * v1.0 zhanghc 2021年10月15日下午1:19:49
 */
public interface UserContextService {

	LoginUser getUser(HttpServletRequest request, HttpServletResponse response, Object handler);

}
