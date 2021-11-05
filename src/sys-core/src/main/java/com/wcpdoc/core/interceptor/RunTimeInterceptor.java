package com.wcpdoc.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wcpdoc.core.context.UserContext;
import com.wcpdoc.core.util.ValidateUtil;

/**
 * 耗时拦截器
 * 
 * v1.0 zhanghc 2021年10月15日上午9:33:58
 */
@Component
public class RunTimeInterceptor implements HandlerInterceptor {
	private static final Logger log = LoggerFactory.getLogger(RunTimeInterceptor.class);
	private static final ThreadLocal<Long> context = new ThreadLocal<>();
	@Value("${runtime.timeout}")
	private Integer TIMEOUT;// 超时时间
	@Value("${runtime.exUrl}")
	private String EXURL;// 排除链接
	@Value("${runtime.monitor}")
	private boolean MONITOR;// 是否监听
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (!MONITOR) {
			return false;
		}
		
		if (ValidateUtil.isValid(EXURL) && EXURL.equals(request.getRequestURI())) {
			return false;
		}
		
		long startTime = System.currentTimeMillis();
		context.set(startTime);
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		TIMEOUT = TIMEOUT == null ? 1 : TIMEOUT;
		TIMEOUT = TIMEOUT < 1000 ? 1000 : TIMEOUT;
		TIMEOUT = TIMEOUT > 10000 ? 10000 : TIMEOUT;// 初始值保证在1至10秒内
		
		long startTime = context.get();
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		if (runTime > TIMEOUT) {
			log.error("请求耗时异常：链接：{}， 耗时：{}毫秒，用户：{}，ip:{}，参数:{}", 
					request.getRequestURI(),
					runTime,
					UserContext.get() != null ? UserContext.get().getLoginName() : "匿名",
					request.getRemoteHost(), 
					request.getParameterMap());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		context.remove();// postHandle抛异常也能保证该代码执行，断点往上翻代码查阅。
	}
}
