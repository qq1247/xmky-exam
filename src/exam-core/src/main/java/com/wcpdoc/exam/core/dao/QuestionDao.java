package com.wcpdoc.exam.core.dao;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionDao extends BaseDao<Question>{

	/**
	 * 更新试题分类
	 * 
	 * v1.0 zhanghc 2021年11月5日下午5:03:27
	 * @param sourceId
	 * @param targetId void
	 */
	void updateQuestionType(Integer sourceId, Integer targetId);
	
	/**
	 * 发布试题
	 * 
	 * v1.0 chenyun 2021年11月11日下午4:30:42
	 * @param questionTypeId
	 * @param userId void
	 */
	void publish(Integer questionTypeId);
}
