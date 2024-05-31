package com.wcpdoc.auth.filter;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.CollectionUtils;
import org.apache.shiro.web.filter.authz.RolesAuthorizationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;

/**
 * 任意角色授权过滤器
 * 
 * v1.0 zhanghc 2021年9月27日下午3:36:34
 */
public class AnyRolesEx extends RolesAuthorizationFilter {

	@Override
	public boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue)
			throws IOException {
		Subject subject = getSubject(request, response);
		String[] rolesArray = (String[]) mappedValue;

		if (rolesArray == null || rolesArray.length == 0) {
			return true;
		}

		Set<String> roles = CollectionUtils.asSet(rolesArray);
		for (String role : roles) {
			if (subject.hasRole(role)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue)
			throws Exception {
		HttpServletResponse httpResponse = WebUtils.toHttp(response);
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json;charset=UTF-8");
		httpResponse.setStatus(HttpStatus.OK.value());
		httpResponse.getWriter()
				.write(String.format("{\"code\": %s, \"msg\": \"未授权\"}", HttpStatus.UNAUTHORIZED.value()));
		return false;
	}
}
