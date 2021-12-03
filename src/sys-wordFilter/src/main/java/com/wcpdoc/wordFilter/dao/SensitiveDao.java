package com.wcpdoc.wordFilter.dao;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.wordFilter.entity.Sensitive;


/**
 * 敏感词数据访问层接口
 * 
 * v1.0 chenyun 2021年9月2日下午4:27:42
 */
public interface SensitiveDao extends RBaseDao<Sensitive> {

	/**
	 * 获取敏感词列表
	 * 
	 * v1.0 chenyun 2021年12月3日下午3:59:37
	 * @return Sensitive
	 */
	Sensitive getList();
}
