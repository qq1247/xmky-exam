package com.wcpdoc.auth.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wcpdoc.auth.service.OnlineUserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 在线用户过滤器
 * 
 * v1.0 zhanghc 2025年11月9日上午10:18:16
 */
@Component
@RequiredArgsConstructor
public class OnlineUserFilter extends OncePerRequestFilter {

	private final OnlineUserService onlineUserService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails userDetails) {
			onlineUserService.heartbeat(userDetails.getUsername(), request.getRemoteAddr());
		}

		chain.doFilter(request, response);
	}
}