package com.wcpdoc.exam.exam.service;

import com.wcpdoc.exam.exam.entity.QuestionType;

/**
 * 试题分类扩展服务层接口
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
	
}

