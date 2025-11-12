package com.wcpdoc.core.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.wcpdoc.core.interceptor.RunTimeInterceptor;
import com.wcpdoc.core.interceptor.UserContextInterceptor;

import lombok.RequiredArgsConstructor;

/**
 * 应用配置
 * 
 * v1.0 zhanghc 2019年9月29日下午2:30:20
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConf implements WebMvcConfigurer {
	private final RunTimeInterceptor runTimeInterceptor;
	private final UserContextInterceptor userContextInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(runTimeInterceptor).addPathPatterns("/api/**");
		registry.addInterceptor(userContextInterceptor).addPathPatterns("/api/**");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		String root = System.getProperty("user.dir") + "/";
		registry.addResourceHandler("/m/**").addResourceLocations("file:" + root + "m/");
		registry.addResourceHandler("/h5/**").addResourceLocations("file:" + root + "h5/");
	}
}
