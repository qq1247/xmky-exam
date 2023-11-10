package com.wcpdoc.auth.cfg;

import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthenticatedException;
import org.apache.shiro.authz.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.entity.PageResult;

/**
 * 全局异常处理
 * 
 * v1.0 zhanghc 2021年3月3日下午5:25:54
 */
@ControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	/**
	 * 异常处理
	 * 
	 * v1.0 zhanghc 2021年3月3日下午3:00:03
	 * 
	 * @param e
	 * @return PageOut
	 */
	@ExceptionHandler(ShiroException.class)
	@ResponseBody
	public PageResult exceptionHandler(Exception se) {
		if (se instanceof IncorrectCredentialsException || se instanceof UnknownAccountException) {
			return PageResult.err().msg(se.getMessage());
		}
		
		if (se instanceof UnauthorizedException) {
			return PageResult.err().code(HttpStatus.UNAUTHORIZED.value()).msg("无访问权限");
		}
		if (se instanceof UnauthenticatedException) {
			return PageResult.err().code(HttpStatus.UNAUTHORIZED.value()).msg("未登陆");
		}

		log.error("shiro未捕获异常：{}", se.getMessage());
		return PageResult.err().code(HttpStatus.UNAUTHORIZED.value());
	}
}
