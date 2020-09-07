package com.wcpdoc.exam.web.conf;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**").allowedOrigins("*").allowCredentials(true).allowedMethods("GET", "POST").maxAge(3600);
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", addcorsConfig());
		return new CorsFilter(source);
	}

	private CorsConfiguration addcorsConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		List<String> list = new ArrayList<>();
		list.add("*");
		corsConfiguration.setAllowedOrigins(list);
		corsConfiguration.addAllowedOrigin("*");
		corsConfiguration.addAllowedHeader("*");
		corsConfiguration.addAllowedMethod("*");
		return corsConfiguration;
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addRedirectViewController("/", "login/toIn");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/api/**")
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/script/**")
				.excludePathPatterns("/img/**");
	}
}
