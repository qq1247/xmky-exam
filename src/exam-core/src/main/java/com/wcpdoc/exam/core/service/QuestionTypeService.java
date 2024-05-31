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
	 * 题库删除
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @param id void
	 */
	void delEx(Integer id);

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
