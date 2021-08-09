package com.wcpdoc.exam.web.conf;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * xss过滤器
 * 
 * v1.0 zhanghc 2020年10月10日上午9:58:23
 */
@WebFilter
public class XssFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(XssFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		// 跨域设置
 		if (response instanceof HttpServletResponse) {
			HttpServletResponse httpServletResponse=(HttpServletResponse)response;
    		//通过在响应 header 中设置 ‘*’ 来允许来自所有域的跨域请求访问。
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
    		//通过对 Credentials 参数的设置，就可以保持跨域 Ajax 时的 Cookie
            //设置了Allow-Credentials，Allow-Origin就不能为*,需要指明具体的url域
            //httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
            //请求方式
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "*");
            //（预检请求）的返回结果（即 Access-Control-Allow-Methods 和Access-Control-Allow-Headers 提供的信息） 可以被缓存多久
            httpServletResponse.setHeader("Access-Control-Max-Age", "86400");
            //首部字段用于预检请求的响应。其指明了实际请求中允许携带的首部字段
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "*");
		}
		
		// sql,xss过滤
 		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
 		if (!(httpServletRequest.getRequestURI().equals("/api/question/add") || httpServletRequest.getRequestURI().equals("/api/question/edit")
 		   || httpServletRequest.getRequestURI().equals("/api/paper/add") || httpServletRequest.getRequestURI().equals("/api/paper/edit")
 		   || httpServletRequest.getRequestURI().equals("/api/exam/add") || httpServletRequest.getRequestURI().equals("/api/exam/edit")
 				)) {
 			if (log.isDebugEnabled()) {
 				log.debug("Xss过滤前：【{}】【{}】", httpServletRequest.getRequestURI(), JSONObject.toJSONString(httpServletRequest.getParameterMap()));
 			}
 			XssHttpServletRequestWrapper xssHttpServletRequestWrapper = new XssHttpServletRequestWrapper(httpServletRequest);
 			chain.doFilter(xssHttpServletRequestWrapper, response);
 			if (log.isDebugEnabled()) {
 				log.debug("Xss过滤后：【{}】【{}】", xssHttpServletRequestWrapper.getRequestURI(), JSONObject.toJSONString(xssHttpServletRequestWrapper.getParameterMap()));
 			}
 			return;
 		}
 		
 		chain.doFilter(httpServletRequest, response);
	}

	@Override
	public void destroy() {

	}
}

/**
 * xss预处理
 * 
 * v1.0 zhanghc 2020年10月10日上午9:58:35
 */
class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {
//	private final Logger log = LoggerFactory.getLogger(XssHttpServletRequestWrapper.class);
	private static Set<String> sqlKeyWords = new HashSet<String>();
	private static Set<String> pageKeyWords = new HashSet<String>(0);
//	private static String replacedString = "INVALID";
	static {
		sqlKeyWords.add("and");
		sqlKeyWords.add("exec");
		sqlKeyWords.add("insert");
		sqlKeyWords.add("select");
		sqlKeyWords.add("delete");
		sqlKeyWords.add("update");
		sqlKeyWords.add("count");
		sqlKeyWords.add("*");
		sqlKeyWords.add("%");
		sqlKeyWords.add("chr");
		sqlKeyWords.add("mid");
		sqlKeyWords.add("master");
		sqlKeyWords.add("truncate");
		sqlKeyWords.add("char");
		sqlKeyWords.add("declare");
		sqlKeyWords.add(";");
		sqlKeyWords.add("or");
		sqlKeyWords.add("-");
		sqlKeyWords.add("+");
		
		pageKeyWords.add("title");//定制非过滤的，通过富文本框处理的
		pageKeyWords.add("optionA");
		pageKeyWords.add("optionB");
		pageKeyWords.add("optionC");
		pageKeyWords.add("optionD");
		pageKeyWords.add("optionE");
		pageKeyWords.add("answer");
		pageKeyWords.add("analysis");
		pageKeyWords.add("description");
	}

//	private String currentUrl;

	public XssHttpServletRequestWrapper(HttpServletRequest servletRequest) {
		super(servletRequest);
//		currentUrl = servletRequest.getRequestURI();
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
		if (pageKeyWords.contains(parameter)) {
			return value;
		}
		
		return cleanXSS(value);
	}

	@Override
	public String[] getParameterValues(String parameter) {
		String[] values = super.getParameterValues(parameter);
		if (values == null) {
			return null;
		}
		if (pageKeyWords.contains(parameter)) {
			return values;
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
			if (pageKeyWords.contains(key)) {
				continue;
			}
			
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
	 * 覆盖getHeader方法，将参数名和参数值都做xss过滤。 如果需要获得原始的值，则通过super.getHeaders(name)来获取
	 * getHeaderNames 也可能需要覆盖
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
		String value = valueP.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		value = value.replaceAll("<", "& lt;").replaceAll(">", "& gt;");
		value = value.replaceAll("\\(", "& #40;").replaceAll("\\)", "& #41;");
		value = value.replaceAll("'", "& #39;");
		value = value.replaceAll("eval\\((.*)\\)", "");
		value = value.replaceAll("[\\\"\\\'][\\s]*javascript:(.*)[\\\"\\\']", "\"\"");
//		value = value.replaceAll("script", "");
		value = cleanSqlKeyWords(value);
		return value;
	}

	private String cleanSqlKeyWords(String value) {
		String paramValue = value;
//		for (String keyword : notAllowedKeyWords) {
//			if (paramValue.length() > keyword.length() + 4 && (paramValue.contains(" " + keyword)
//					|| paramValue.contains(keyword + " ") || paramValue.contains(" " + keyword + " "))) {
//				paramValue = StringUtils.replace(paramValue, keyword, replacedString);
//				log.error(this.currentUrl + "已被过滤，因为参数中包含不允许sql的关键词(" + keyword + ")" + ";参数：" + value + ";过滤后的参数："
//						+ paramValue);
//			}
//		}
		return paramValue;
	}
}