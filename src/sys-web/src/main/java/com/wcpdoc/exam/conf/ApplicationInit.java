package com.wcpdoc.exam.conf;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.util.SpringUtil;
import com.wcpdoc.exam.sys.cache.DictCache;
import com.wcpdoc.exam.sys.cache.ResCache;

/**
 * 应用初始化
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class ApplicationInit implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		ResCache.flushCache();
		DictCache.flushCache();

		ServletContext servletContext = SpringUtil.getBean(ServletContext.class);
		servletContext.setAttribute(ConstantManager.SESSION_USER_LIST, new HashMap<String, HttpSession>());

		servletContext.setAttribute(ConstantManager.MENU_LIST, ResCache.getMenuList());
		servletContext.setAttribute(ConstantManager.HOME_MENU_LIST, ResCache.getHomeMenuList());
	}
}
