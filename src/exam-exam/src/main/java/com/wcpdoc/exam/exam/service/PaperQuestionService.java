package com.wcpdoc.exam.exam.service;

import java.util.List;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.PaperQuestion;

/**
 * 试卷试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface PaperQuestionService extends BaseService<PaperQuestion>{

	/**
	 * 保存试卷试题
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:45:59
	 * @param paperQuestion
	 * void
	 */
	void saveAndUpdate(PaperQuestion paperQuestion);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年5月27日上午10:50:04
	 * @param parentId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getList(Integer parentId);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年6月5日下午6:30:24
	 * @param paperId
	 * @return PaperQuestion
	 */
	PaperQuestion getTopPaperQuestion(Integer paperId);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年6月29日下午2:49:32
	 * @param paperId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getPaperQuestionList(Integer paperId);

	/**
	 * 获取试卷试题
	 * 
	 * v1.0 zhanghc 2017年8月24日上午8:57:31
	 * @param paperId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer paperId, Integer questionId);
	
}
