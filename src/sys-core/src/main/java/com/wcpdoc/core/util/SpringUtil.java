package com.wcpdoc.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring工具类
 * 
 * v1.0 zhanghc 2019年4月12日下午1:53:19
 */
@Component
public class SpringUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		SpringUtil.applicationContext = applicationContext;
	}

	/**
	 * 获取spring容器
	 * 
	 * v1.0 zhanghc 2019年4月12日下午1:55:51
	 * 
	 * @return ApplicationContext
	 */
	public static ApplicationContext getContext() {
		return applicationContext;
	}

	/**
	 * 获取bean
	 * 
	 * v1.0 zhanghc 2019年4月12日下午1:55:58
	 * 
	 * @param t
	 * @return T
	 */
	public static <T> T getBean(Class<T> t) {
		return applicationContext.getBean(t);
	}

	/**
	 * 获取bean
	 * 
	 * v1.0 zhanghc 2019年4月12日下午1:55:58
	 * 
	 * @param t
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String className) {
		return (T) applicationContext.getBean(className);
	}
}