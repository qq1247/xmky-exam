package com.wcpdoc.plugin.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 插件权限注解
 * 
 * 标注在 PluginService.execute() 方法上，声明允许执行的角色（OR 关系）
 * 
 * v1.0 zhanghc 2025年11月14日下午1:45:13
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginRoles {

	/**
	 * 角色角色（or关系）
	 * 
	 * v1.0 zhanghc 2025年11月14日下午1:46:36
	 * 
	 * @return String[]
	 */
	String[] value();

}