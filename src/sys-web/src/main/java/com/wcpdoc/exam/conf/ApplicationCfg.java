package com.wcpdoc.exam.conf;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 应用配置
 * 
 * v1.0 zhanghc 2019年9月29日下午2:30:20
 */
@Configuration
public class ApplicationCfg implements WebMvcConfigurer {
	@Resource
	private AuthInterceptor authInterceptor;

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "home/toIn");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/script/**")
				.excludePathPatterns("/img/**");
	}
}
