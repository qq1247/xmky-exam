package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
/**
 * 试卷试题答案服务层接口
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
public interface PaperQuestionAnswerService extends BaseService<PaperQuestionAnswer>{

	/**
	 * 获取试卷答案
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:52:51
	 * @param paperId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getList(Integer paperId);
	
	/**
	 * 获取试卷答案
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:52:51
	 * @param paperId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getList(Integer examId, Integer userId);
	
	/**
	 * 获取试卷答案
	 * 
	 * v1.0 zhanghc 2022年5月22日下午3:56:18
	 * @param paperId
	 * @param questionId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getListForSingleQuestion(Integer paperId, Integer questionId);

}
