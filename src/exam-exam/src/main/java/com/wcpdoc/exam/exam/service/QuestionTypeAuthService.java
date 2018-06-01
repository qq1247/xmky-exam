package com.wcpdoc.exam.exam.service;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;

/**
 * 试题分类权限服务层接口
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
public interface QuestionTypeAuthService extends BaseService<QuestionTypeAuth>{

	/**
	 * 删除试题分类权限
	 * 
	 * v1.0 zhanghc 2018年5月30日下午8:21:23
	 * @param questionTypeId 
	 * void
	 */
	void delByQuestionTypeId(Integer questionTypeId);

	/**
	 * 获取试题分类权限
	 * 
	 * v1.0 zhanghc 2018年5月30日下午8:36:04
	 * @param questionTypeId
	 * @return QuestionTypeAuth
	 */
	QuestionTypeAuth getEntityByQuestionTypeId(Integer questionTypeId);
	
}
