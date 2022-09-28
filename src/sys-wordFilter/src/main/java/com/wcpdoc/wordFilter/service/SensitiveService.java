package com.wcpdoc.wordFilter.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.wordFilter.entity.Sensitive;

/**
 * 敏感词服务层接口
 * 
 * v1.0 chenyun 2021年9月2日下午4:28:34
 */
public interface SensitiveService extends BaseService<Sensitive> {

	/**
	 * 添加修改敏感词
	 * 
	 * v1.0 chenyun 2021年9月3日上午10:36:12
	 * @param sensitive
	 * @return void
	 */
	void updateEx(Sensitive sensitive);
	
	/**
	 * 屏蔽敏感词
	 * 
	 * v1.0 chenyun 2021年9月3日下午1:23:53
	 * @param content
	 * @return String
	 */
	String replace(String content);
	
	/**
	 * 初始化敏感词
	 * 
	 * v1.0 chenyun 2021年9月28日下午5:38:30 void
	 */
	void init();
}
