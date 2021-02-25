package com.wcpdoc.exam.web.conf;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 应用配置
 * 
 * v1.0 chenyun 2020年11月4日下午3:59:45
 */
@Configuration
public class ApplicationCfg implements WebMvcConfigurer  {
	@Resource
	private AuthInterceptor authInterceptor;	
	
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "login/toIn");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	registry.addInterceptor(authInterceptor).addPathPatterns("/**")
			.excludePathPatterns("/css/**")
			.excludePathPatterns("/script/**")
			.excludePathPatterns("/img/**");
	}
}