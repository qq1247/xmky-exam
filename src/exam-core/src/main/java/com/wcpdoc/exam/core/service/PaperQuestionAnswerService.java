package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
/**
 * 试卷试题答案服务层接口
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
public interface PaperQuestionAnswerService extends BaseService<PaperQuestionAnswer>{
	
	/**
	 * 获取试卷试题答案列表
	 * 
	 * v1.0 chenyun 2021年7月23日上午11:27:30
	 * @param paperQuestionId
	 * @param questionId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getPaperQuestionAnswerList(Integer paperQuestionId, Integer questionId);
	
	/**
	 * 删除试卷试题答案
	 * 
	 * v1.0 chenyun 2021年7月21日下午7:08:30
	 * @param id void
	 */
	void updateAndDel(Integer id);
}
