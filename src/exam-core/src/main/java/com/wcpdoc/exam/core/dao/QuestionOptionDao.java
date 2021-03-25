package com.wcpdoc.exam.core.dao;

import com.wcpdoc.exam.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 试题选项数据访问层接口
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
public interface QuestionOptionDao extends RBaseDao<QuestionOption> {
	
	/**
	 * 获取试题
	 * 
	 * v1.0 chenyun 2021年3月18日下午2:25:45
	 * @param questionId
	 * @return QuestionOption
	 */
	QuestionOption getQuestionOption(Integer questionId);
}
