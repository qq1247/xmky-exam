package com.wcpdoc.exam.exam.dao;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;

/**
 * 分类权限数据访问层接口
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
public interface QuestionTypeAuthDao extends BaseDao<QuestionTypeAuth>{

	/**
	 * 删除试题分类权限
	 * 
	 * v1.0 zhanghc 2018年5月30日下午8:21:23
	 * @param questionTypeId 
	 * void
	 */
	void delQuestionType(Integer questionTypeId);

	/**
	 * 获取试题分类权限
	 * 
	 * v1.0 zhanghc 2018年5月30日下午8:36:40
	 * @param questionTypeId
	 * @return QuestionTypeAuth
	 */
	QuestionTypeAuth getQuestionTypeEntity(Integer questionTypeId);
	
}
