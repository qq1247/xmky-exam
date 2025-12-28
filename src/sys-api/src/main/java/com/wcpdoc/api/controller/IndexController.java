package com.wcpdoc.api.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 首页控制层
 * 
 * v1.0 zhanghc 2025年11月9日下午4:55:49
 */
@Controller
public class IndexController implements ErrorController {

	/**
	 * 模拟nginx路由（简化部署难度）
	 * 
	 * v1.0 zhanghc 2025年12月27日下午3:31:26
	 * 
	 * @param request
	 * @return String
	 */
	@RequestMapping("/error")
	public String error(HttpServletRequest request) {
		Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String requestURI = request.getRequestURI();
		if (requestURI.startsWith("/api/")) {
			return null;
		}
		if (statusCode != null && statusCode == HttpStatus.NOT_FOUND.value()) {
			return isMobileDevice(request) ? "forward:/m/index.html" : "forward:/h5/index.html";
		}

		return null;

	}

	/**
	 * 静态资源路由
	 * 
	 */
	@RequestMapping({ "/assets/**", "/img/**", "/static/**", "/uni_modules/**", "/plugins/**", "/config.js",
			"/favicon.ico" })
	public String resources(HttpServletRequest request) {
		String path = request.getRequestURI();
		return isMobileDevice(request) ? "forward:/m" + path : "forward:/h5" + path;
	}

	/**
	 * 是否移动端设备访问
	 * 
	 * v1.0 zhanghc 2025年12月27日下午4:17:53
	 * 
	 * @param request
	 * @return boolean
	 */
	private boolean isMobileDevice(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null)
			return false;

		String lowerUA = userAgent.toLowerCase();
		return lowerUA.contains("mobile") || lowerUA.contains("android") || lowerUA.contains("iphone")
				|| lowerUA.contains("ipod") || lowerUA.contains("iemobile") || lowerUA.contains("windows phone");
	}
}