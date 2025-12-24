package com.wcpdoc.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wcpdoc.core.controller.BaseController;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 首页控制层
 * 
 * v1.0 zhanghc 2025年11月9日下午4:55:49
 */
@Controller
public class IndexController extends BaseController {

	/**
	 * 首页路由<br/>
	 * 为简化安装，后端支持多端登录
	 * 
	 * v1.0 zhanghc 2025年11月9日下午4:57:32
	 * 
	 * @param request
	 * @return String
	 */
	@GetMapping("/")
	public String serveIndex(HttpServletRequest request) {
		return isMobileDevice(request) ? "forward:/m/index.html" : "forward:/h5/index.html";
	}

	/**
	 * 静态资源路由
	 * 
	 */
	@RequestMapping({ "/assets/**", "/img/**", "/static/**", "/uni_modules/**", "/plugins/**", "/config.js",
			"/favicon.ico" })
	public String serveStatic(HttpServletRequest request) {
		String path = request.getRequestURI();
		return isMobileDevice(request) ? "forward:/m" + path : "forward:/h5" + path;
	}

	private boolean isMobileDevice(HttpServletRequest request) {
		String userAgent = request.getHeader("User-Agent");
		if (userAgent == null)
			return false;

		String lowerUA = userAgent.toLowerCase();
		return lowerUA.contains("mobile") || lowerUA.contains("android") || lowerUA.contains("iphone")
				|| lowerUA.contains("ipod") || lowerUA.contains("iemobile") || lowerUA.contains("windows phone");
	}
}