package com.wcpdoc.auth.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * 演示模式过滤器
 * 
 * v1.0 zhanghc 2025年4月3日上午9:10:02
 */
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "demo")
@Data
@EqualsAndHashCode(callSuper = false)
public class DemoModeFilter extends OncePerRequestFilter {
	private boolean mode;

	private static final List<String> DEMO_PROTECTED_PATHS = Arrays.asList("/api/**/del", "/api/parm/ent",
			"/api/parm/email", "/api/parm/file", "/api/parm/db", "/api/parm/pwd", "/api/parm/custom", "/api/parm/m",
			"/api/cron/add", "/api/cron/edit", "/api/cron/start-task", "/api/cron/stop-task", "/api/cron/run-once-task",
			"/api/dict/add", "/api/dict/edit", "/api/org/add", "/api/org/edit", "/api/org/move", "/api/user/edit",
			"/api/user/frozen", "/api/user/pwd-init", "/api/login/pwd", "/api/question-bank/edit",
			"/api/question-bank/clear");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (mode) {
			String path = request.getRequestURI();
			AntPathMatcher matcher = new AntPathMatcher();
			for (String pattern : DEMO_PROTECTED_PATHS) {
				if (matcher.match(pattern, path)) {
					response.setStatus(HttpStatus.OK.value());
					response.setContentType("application/json;charset=UTF-8");
					response.getWriter().write("{\"code\":403,\"msg\":\"演示模式\"}");
					return;
				}
			}
		}

		filterChain.doFilter(request, response);
	}
}