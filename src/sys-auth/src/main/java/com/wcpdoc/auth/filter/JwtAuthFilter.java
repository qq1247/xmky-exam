package com.wcpdoc.auth.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.wcpdoc.auth.entity.JwtToken;
import com.wcpdoc.auth.entity.UserDetailsImpl;
import com.wcpdoc.auth.service.JwtTokenService;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * jwt授权认证过滤器
 * 
 * v1.0 zhanghc 2021年2月25日下午2:22:51 <br/>
 * 使用apache-shiro实现
 * 
 * v1.1 zhanghc 2025-11-03 22:33:00 <br/>
 * 切换为spring-security
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtTokenService jwtTokenService;
	private final BaseCacheService baseCacheService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String token = request.getHeader("Authorization");
		if (jwtTokenService.isValid(token)) {
			JwtToken jwtToken = jwtTokenService.parse(token);
			User user = baseCacheService.getUser(jwtToken.getUserId());

			List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
			UserDetails userDetails = new UserDetailsImpl(user);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
					authorities);
			auth.setDetails(new WebAuthenticationDetails(request));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

		chain.doFilter(request, response);
	}
}