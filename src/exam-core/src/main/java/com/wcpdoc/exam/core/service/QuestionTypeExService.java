package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 题库扩展服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeExService {

	/**
	 * 题库删除
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @param questionType void
	 */
	void delEx(QuestionType questionType);

	/**
	 * 题库合并
	 * 
	 * v1.0 zhanghc 2022年6月16日下午4:44:00
	 * 
	 * @param sourceId
	 * @param targetId void
	 */
	void move(Integer sourceId, Integer targetId);

	/**
	 * 题库清空
	 * 
	 * v1.0 zhanghc 2022年9月15日上午9:28:44
	 * 
	 * @param id
	 * @return PageResult
	 */
	void clear(Integer id);
}
