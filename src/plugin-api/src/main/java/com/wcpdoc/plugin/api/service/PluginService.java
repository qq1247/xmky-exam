package com.wcpdoc.plugin.api.service;

import java.util.Map;

import org.pf4j.ExtensionPoint;

import reactor.core.publisher.Flux;

/**
 * 插件业务层接口
 * 
 * v1.0 zhanghc 2025年11月13日下午5:12:53
 */
public interface PluginService extends ExtensionPoint {
	/**
	 * 插件ID
	 * 
	 * v1.0 zhanghc 2025年11月15日下午5:25:52
	 * 
	 * @return String
	 */
	String getId();

	/**
	 * 插件名称
	 * 
	 * v1.0 zhanghc 2025年11月13日下午5:25:15
	 * 
	 * @return String
	 */
	String getName();

	/**
	 * 版本
	 * 
	 * v1.0 zhanghc 2025年11月15日下午5:27:11
	 * 
	 * @return String
	 */
	String getVer();

	/**
	 * 简要描述
	 * 
	 * v1.0 zhanghc 2025年11月15日下午5:27:35
	 * 
	 * @return String
	 */
	String getDesc();

	/**
	 * 插件执行
	 * 
	 * v1.0 zhanghc 2025年11月14日下午4:15:57
	 * 
	 * @param requestParm
	 * @return Object
	 */
	Object execute(Map<String, Object> requestParm);

	/**
	 * 插件执行
	 * 
	 * v1.0 zhanghc 2025年11月14日下午4:15:57
	 * 
	 * @param requestParm
	 * @return Mono<Void>
	 */
	Flux<String> run(Map<String, Object> requestParm);
}
