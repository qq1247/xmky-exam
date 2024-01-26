package com.wcpdoc.core.conf;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wcpdoc.core.interceptor.OnlineUserInterceptor;
import com.wcpdoc.core.interceptor.RunTimeInterceptor;
import com.wcpdoc.core.interceptor.UserContextInterceptor;

/**
 * 应用配置
 * 
 * v1.0 zhanghc 2019年9月29日下午2:30:20
 */
@Configuration
public class ApplicationConf implements WebMvcConfigurer {
	@Resource
	private UserContextInterceptor userContextInterceptor;
	@Resource
	private RunTimeInterceptor runTimeInterceptor;
	@Resource
	private OnlineUserInterceptor onlineUserInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(userContextInterceptor).addPathPatterns("/api/**");
		registry.addInterceptor(runTimeInterceptor).addPathPatterns("/api/**");
		registry.addInterceptor(onlineUserInterceptor).addPathPatterns("/api/**");
	}
}
