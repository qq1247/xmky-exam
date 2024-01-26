package com.wcpdoc.core.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * 跨域过滤器
 * 
 * v1.0 chenyun 2021年12月27日上午10:51:31
 */
@WebFilter
public class CrossDomainFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (response instanceof HttpServletResponse) {
			httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
			httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
			httpServletResponse.setHeader("Access-Control-Allow-Headers", "Authorization,content-type");
			httpServletResponse.setHeader("Access-Control-Max-Age", "86400");
		}
		chain.doFilter(request, httpServletResponse);
	}
}
