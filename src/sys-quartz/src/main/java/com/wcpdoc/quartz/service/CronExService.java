package com.wcpdoc.quartz.service;

/**
 * 定时任务扩展服务层接口
 * 
 * v1.0 chenyun 2021年11月12日下午2:03:37
 */
public interface CronExService {

	/**
	 * 获取缓存值
	 * 
	 * v1.0 chenyun 2021年11月12日下午2:03:37
	 * 
	 * @return String
	 */
	String getDbBakDir();
}
