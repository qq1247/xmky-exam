package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 试题选项数据访问层接口
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
public interface QuestionOptionDao extends RBaseDao<QuestionOption> {
	
	/**
	 * 获取试题选项列表
	 * 
	 * v1.0 chenyun 2021年3月18日下午2:25:45
	 * @param questionId
	 * @return List<QuestionOption>
	 */
	List<QuestionOption> getList(Integer questionId);
}
