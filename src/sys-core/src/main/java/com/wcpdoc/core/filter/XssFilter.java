package com.wcpdoc.core.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.text.StringEscapeUtils;

/**
 * xss过滤器
 * 
 * v1.0 chenyun 2021年12月28日上午9:12:11
 */
@WebFilter
public class XssFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

//		if (log.isDebugEnabled()) {
//			log.debug("Xss过滤前：【{}】【{}】", httpServletRequest.getRequestURI(),
//					JSONObject.toJSONString(httpServletRequest.getParameterMap()));
//		}
		XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(
				httpServletRequest);
		chain.doFilter(xssHttpServletRequestWrapper, response);
//		if (log.isDebugEnabled()) {
//			log.debug("Xss过滤后：【{}】【{}】", xssHttpServletRequestWrapper.getRequestURI(),
//					JSONObject.toJSONString(xssHttpServletRequestWrapper.getParameterMap()));
//		}
	}

	@Override
	public void destroy() {

	}
}

/**
 * xss预处理
 * 
 * v1.0 chenyun 2021年12月28日上午11:00:38
 */
class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
	public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
	}

	/**
	 * 覆盖getParameter方法，将参数名和参数值都做xss过滤。
	 * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
	 * getParameterNames,getParameterValues和getParameterMap也可能需要覆盖
	 */
	@Override
	public String getParameter(String parameter) {
		String value = super.getParameter(parameter);
		if (value == null) {
			return null;
		}

		return cleanXSS(value);
	}

	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}

		int count = values.length;
		String[] encodedValues = new String[count];
		for (int i = 0; i < count; i++) {
			encodedValues[i] = cleanXSS(values[i]);
		}
		return encodedValues;
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> values = super.getParameterMap();
		if (values == null) {
			return null;
		}
		Map<String, String[]> result = new HashMap<>();
		for (String key : values.keySet()) {

			String encodedKey = cleanXSS(key);
			int count = values.get(key).length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = cleanXSS(values.get(key)[i]);
			}
			result.put(encodedKey, encodedValues);
		}
		return result;
	}

	/**
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。 <br/>
	 * 如果需要获得原始的值，则通过super.getHeaders(name)来获取 getHeaderNames 也可能需要覆盖
	 */
	@Override
	public String getHeader(String name) {
		String value = super.getHeader(name);
		if (value == null) {
			return null;
		}
		return cleanXSS(value);
	}

	private String cleanXSS(String valueP) {
		// String ssString = "<alert>(123)ni你我wo(*&^%$#@!)</alert>";
		return StringEscapeUtils.escapeHtml3(valueP);
	}
}
