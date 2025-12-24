package com.wcpdoc.auth.conf;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.wcpdoc.auth.filter.JwtAuthFilter;
import com.wcpdoc.auth.filter.OnlineUserFilter;

import lombok.RequiredArgsConstructor;

/**
 * 授权认证配置
 * 
 * v1.0 zhanghc 2021年2月25日下午2:22:51 <br/>
 * 使用apache-shiro实现
 * 
 * v1.1 zhanghc 2025-11-03 22:33:00 <br/>
 * 切换为spring-security
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthConf {
	private final OnlineUserFilter onlineUserFilter;
	private final JwtAuthFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()); // 禁用 CSRF（适用于无状态 JWT 认证）
		http.cors(cors -> cors.configurationSource(request -> { // 启用 CORS，并配置允许的源、方法、头
			CorsConfiguration config = new CorsConfiguration();
			config.setAllowedOriginPatterns(List.of("*"));
			config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Refresh-Token"));
			config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
			config.setMaxAge(3600L);
			return config;
		}));
		http.formLogin(form -> form.disable()); // 禁用表单登录
		http.httpBasic(httpBasic -> httpBasic.disable()); // 禁用 http basic 认证
		http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 禁用httpSession

		http.authorizeHttpRequests(authz -> authz // 权限规则配置
				.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
				// ========== 匿名访问 ==========
				.requestMatchers(//
						"/api/login/**", //
						"/api/file/download", //
						"/api/exam/exam-get", //
						"/api/plugin/**" //
				).permitAll()
				// ========== 登录访问，不限角色 ==========
				.requestMatchers(//
						"/api/file/upload", //
						"/api/dict/index-list", //
						"/api/bulletin/listpage", //
						"/api/bulletin/get", //
						"/api/user/get", //
						"/api/progress-bar/**"//
				).authenticated()
				// ========== 管理员、子管理员 ==========
				.requestMatchers(//
						"/api/user/listpage", //
						"/api/org/listpage", //
						"/api/user/add", //
						"/api/user/edit", //
						"/api/user/del", //
						"/api/user/pwd-init", //
						"/api/user/frozen", //
						"/api/parm/get", //
						"/api/report/exam/rank-listpage", //
						"/api/report/exam/statis", //
						"/api/report/paper/export-pdf", //
						"/api/report/rank/export-pdf", //
						"/api/report/exer/track-listpage", //
						"/api/report/exer/wrong-question-listpage", //
						"/api/question-bank/**", //
						"/api/question/**", //
						"/api/exam/**", //
						"/api/exer/**"//
				)//
				.hasAnyRole("ADMIN", "SUB_ADMIN")//
				// ========== 管理员、子管理员、阅卷用户 ==========
				.requestMatchers("/api/my-mark/**")//
				.hasAnyRole("ADMIN", "SUB_ADMIN", "MARK_USER")//
				// ========== 考试用户、临时用户 ==========
				.requestMatchers("/api/my-exam/**")//
				.hasAnyRole("EXAM_USER", "TEMP_USER")//
				// ========== 考试用户 ==========
				.requestMatchers("/api/my-exer/**", "/api/my-comment/**")//
				.hasAnyRole("EXAM_USER")//
				// ========== 管理员 ==========
				.requestMatchers("/api/**")//
				.hasRole("ADMIN")//
				.anyRequest()//
				.permitAll());

		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // 提前通过jwt完成用户认证
		http.addFilterBefore(onlineUserFilter, UsernamePasswordAuthenticationFilter.class); // 记录在线用户

		http.exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> { // 自定义认证失败（401）和权限不足（403）的响应
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"code\":401,\"msg\":\"登录已过期，请重新登录\"}");
		}).accessDeniedHandler((request, response, accessDeniedException) -> {
			response.setStatus(HttpStatus.FORBIDDEN.value());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write("{\"code\":403,\"msg\":\"没有权限访问此功能\"}");
		}));

		return http.build();
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return username -> {
			throw new UsernameNotFoundException("未实现"); // 使用loginName不能从缓存查询
		};
	}

	@Bean
	public GrantedAuthorityDefaults grantedAuthorityDefaults() {
		return new GrantedAuthorityDefaults(""); // 去掉ROLE_前缀
	}
}