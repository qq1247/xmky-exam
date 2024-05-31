package com.wcpdoc.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.wcpdoc.core.context.UserContext;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 耗时拦截器
 * 
 * v1.0 zhanghc 2021年10月15日上午9:33:58
 */
@Component
@Slf4j
public class RunTimeInterceptor implements HandlerInterceptor {
	private static final ThreadLocal<Long> context = new ThreadLocal<>();
	@Value("${runtime.timeout}")
	private Integer TIME_OUT;// 超时时间
	@Value("${runtime.exUrl}")
	private String EX_URL;// 排除链接
	@Value("${runtime.monitor}")
	private boolean MONITOR;// 是否监听

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		long startTime = System.currentTimeMillis();
		context.set(startTime);
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (!MONITOR) {
			return;
		}

		if (ValidateUtil.isValid(EX_URL) && EX_URL.contains(request.getRequestURI())) {
			return;
		}

		if (!ValidateUtil.isValid(TIME_OUT)) {
			TIME_OUT = TIME_OUT == null ? 1 : TIME_OUT;
			TIME_OUT = TIME_OUT < 1000 ? 1000 : TIME_OUT;
			TIME_OUT = TIME_OUT > 3000 ? 3000 : TIME_OUT;// 初始值保证在1至3秒内
		}

		long startTime = context.get();
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;
		if (runTime > TIME_OUT) {
			log.error("请求耗时异常：链接：{}， 耗时：{}毫秒，用户：{}，ip:{}，参数:{}", request.getRequestURI(), runTime,
					UserContext.get() != null ? UserContext.get().getLoginName() : "匿名", request.getRemoteHost(),
					request.getParameterMap());
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		context.remove();// postHandle抛异常也能保证该代码执行，断点往上翻代码查阅。
	}
}
