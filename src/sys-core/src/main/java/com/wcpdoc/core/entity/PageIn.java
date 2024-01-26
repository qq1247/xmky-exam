package com.wcpdoc.core.entity;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wcpdoc.core.util.ValidateUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 页面输入
 * 
 * v1.0 zhanghc 2015-6-19下午08:30:16
 * 
 * @param <T>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageIn {
	private Integer curPage = 1;
	private Integer pageSize = 20;
	private HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
			.getRequest();

	public PageIn addParm(String key, Object value) {
		request.setAttribute(key, value);
		return this;// 用于链式调用
	}

	public Object getParm(String key) {
		return getParm(key, Object.class);
	}

	@SuppressWarnings("unchecked")
	public <T> T getParm(String key, Class<T> t) {
		String parameter = request.getParameter(key);
		if (ValidateUtil.isValid(parameter)) {
			return (T) parameter;
		}
		if (request == null) {// 非前端调用时，request会为空
			return null;
		}
		return (T) request.getAttribute(key);
	}

	public boolean hasParm(String key) {
		String parameter = request.getParameter(key);
		if (ValidateUtil.isValid(parameter)) {
			return true;
		}
		if (request == null) {// 非前端调用时，request会为空
			return false;
		}
		return request.getAttribute(key) != null;
	}

	public <T> Page<T> toPage() {
		return Page.of(curPage, pageSize);
	}
}
