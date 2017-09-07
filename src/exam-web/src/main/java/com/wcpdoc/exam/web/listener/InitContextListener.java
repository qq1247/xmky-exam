package com.wcpdoc.exam.web.listener;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.sys.cache.DictCache;
import com.wcpdoc.exam.sys.cache.ResCache;

public class InitContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//初始化缓存
		ResCache.flushCache();
		DictCache.flushCache();
		
		ServletContext servletContext = sce.getServletContext();
		servletContext.setAttribute(ConstantManager.SESSION_USER_LIST, new HashMap<String, HttpSession>());
		
		servletContext.setAttribute(ConstantManager.MENU_LIST, ResCache.getMenuList());
		servletContext.setAttribute(ConstantManager.HOME_MENU_LIST, ResCache.getHomeMenuList());
		
		// WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(sce.getServletContext());
		//WebApplicationContext springContext = ContextLoader.getCurrentWebApplicationContext();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {

	}
}
