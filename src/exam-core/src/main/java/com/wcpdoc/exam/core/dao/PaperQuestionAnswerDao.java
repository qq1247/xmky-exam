package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;

/**
 * 试题答案数据访问层接口
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
public interface PaperQuestionAnswerDao extends BaseDao<PaperQuestionAnswer>{
	
	/**
	 * 获取试卷试题答案列表
	 * 
	 * v1.0 chenyun 2021年7月23日上午11:27:30
	 * @param paperId
	 * @param questionId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getList(Integer paperId, Integer questionId);
}
