package com.wcpdoc.auth.conf;

import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 * 
 * v1.0 zhanghc 2021年3月3日下午5:25:54
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 异常处理
	 * 
	 * v1.0 zhanghc 2021年3月3日下午3:00:03
	 * 
	 * @param e
	 * @return PageOut
	 */
//	@ExceptionHandler(ShiroException.class)
//	public PageResult exceptionHandler(Exception se) {
//		if (se instanceof IncorrectCredentialsException || se instanceof UnknownAccountException) {
//			return PageResult.err().msg(se.getMessage());
//		}
//
//		if (se instanceof UnauthorizedException) {
//			return PageResult.err().code(HttpStatus.UNAUTHORIZED.value()).msg("无访问权限");
//		}
//		if (se instanceof UnauthenticatedException) {
//			return PageResult.err().code(HttpStatus.UNAUTHORIZED.value()).msg("未登陆");
//		}
//
//		log.error("shiro未捕获异常：{}", se.getMessage());
//		return PageResult.err().code(HttpStatus.UNAUTHORIZED.value());
//	}
}
