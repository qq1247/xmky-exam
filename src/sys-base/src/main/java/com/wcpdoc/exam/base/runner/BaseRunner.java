package com.wcpdoc.exam.base.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 基础服务启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class BaseRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		/*ResCache.flushCache();
		DictCache.flushCache();

		ServletContext servletContext = SpringUtil.getBean(ServletContext.class);
		servletContext.setAttribute(ConstantManager.SESSION_USER_LIST, new HashMap<String, HttpSession>());

		servletContext.setAttribute(ConstantManager.MENU_LIST, ResCache.getBackMenuList());
		servletContext.setAttribute(ConstantManager.HOME_MENU_LIST, ResCache.getHomeMenuList());*/
	}
}
