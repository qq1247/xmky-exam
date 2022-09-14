package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionType;
/**
 * 题库服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeService extends BaseService<QuestionType> {
	
	/**
	 * 题库添加
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param questionType
	 * void
	 */
	void addAndUpdate(QuestionType questionType);

	/**
	 * 题库修改
	 * 
	 * v1.0 chenyun 2021年3月18日上午10:20:28
	 * @param questionType
	 * void
	 */
	void editAndUpdate(QuestionType questionType);
	
	/**
	 * 题库删除
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);

	/**
	 * 题库合并
	 * 
	 * v1.0 zhanghc 2022年6月16日下午4:44:00
	 * @param sourceId
	 * @param targetId void
	 */
	void move(Integer sourceId, Integer targetId);

}
