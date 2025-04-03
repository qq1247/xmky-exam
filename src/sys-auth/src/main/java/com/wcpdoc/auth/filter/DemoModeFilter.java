package com.wcpdoc.auth.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;

/**
 * jwt过滤器
 * 
 * v1.0 zhanghc 2021年3月2日上午9:52:04
 */
public class DemoModeFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 演示模式下直接拒绝操作
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
    	HttpServletResponse httpResponse = WebUtils.toHttp(response);
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setStatus(HttpStatus.OK.value());
		httpResponse.getWriter()
				.write(String.format("{\"code\": %s, \"msg\": \"演示模式\"}", HttpStatus.FORBIDDEN.value()));
		return false;
    }
}