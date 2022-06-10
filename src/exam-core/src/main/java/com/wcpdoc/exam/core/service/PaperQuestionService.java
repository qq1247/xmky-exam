package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.PaperQuestion;

/**
 * 试卷试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface PaperQuestionService extends BaseService<PaperQuestion>{
	
	/**
	 * 获取章节列表
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:43:46
	 * @param paperId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getChapterList(Integer paperId);
	
	/**
	 * 获取试卷详细列表
	 * 
	 * v1.0 zhanghc 2017年5月27日上午10:51:44
	 * @param chapterId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getChapterDetailList(Integer chapterId);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2022年5月24日上午10:06:53
	 * @param paperId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer paperId, Integer questionId);
	
	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 chenyun 2022年2月15日上午9:56:07
	 * @param userId
	 * @param paperId void
	 */
	List<PaperQuestion> getList(Integer examId, Integer userId);

	/**
	 * 获取试卷试题实体
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:29:05
	 * @param id
	 * @param userId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer id, Integer userId, Integer questionId);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:28:58
	 * @param questionId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getList(Integer questionId);
}
