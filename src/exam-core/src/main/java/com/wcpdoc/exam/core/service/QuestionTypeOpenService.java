package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.QuestionTypeOpen;

/**
 * 试题分类开放服务层接口
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
public interface QuestionTypeOpenService extends BaseService<QuestionTypeOpen> {
	
	/**
	 * 添加试题分类开放
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param id
	 * void
	 */
	void addAndUpdate(QuestionTypeOpen questionTypeOpen);
	
	/**
	 * 删除试题分类开放
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
}
