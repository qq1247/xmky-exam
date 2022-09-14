package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 题库扩展服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeExService{

	/**
	 * 删除关联引用
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param questionType 
	 * void
	 */
	void delAndUpdate(QuestionType questionType);

	/**
	 * 移动
	 * 
	 * v1.0 zhanghc 2022年6月16日下午4:44:00
	 * @param sourceId
	 * @param targetId void
	 */
	void move(Integer sourceId, Integer targetId);
}

