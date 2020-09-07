package com.wcpdoc.exam.base.runner;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.cache.ResCache;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.util.SpringUtil;

/**
 * 基础服务启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class BaseRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		ResCache.flushCache();
		DictCache.flushCache();

		ServletContext servletContext = SpringUtil.getBean(ServletContext.class);
		servletContext.setAttribute(ConstantManager.SESSION_USER_LIST, new HashMap<String, HttpSession>());

		servletContext.setAttribute(ConstantManager.MENU_LIST, ResCache.getBackMenuList());
		servletContext.setAttribute(ConstantManager.HOME_MENU_LIST, ResCache.getHomeMenuList());
	}
}
