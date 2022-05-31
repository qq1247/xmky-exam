package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PaperQuestion;

/**
 * 试卷试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface PaperQuestionDao extends BaseDao<PaperQuestion>{
	/**
	 * 获取章节列表
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:43:46
	 * @param paperId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getChapterList(Integer paperId);

	/**
	 * 获取章节详细列表
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:58:11
	 * @param chapterId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getChapterDetailList(Integer chapterId);

	/**
	 * 获取试卷试题实体
	 * 
	 * v1.0 zhanghc 2022年5月24日上午10:07:36
	 * @param paperId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer paperId, Integer questionId);
	
	/**
	 * 获取试卷试题实体
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:22:35
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return PaperQuestion
	 */
	PaperQuestion getEntity(Integer examId, Integer userId, Integer questionId);
	
	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 chenyun 2022年2月15日上午9:56:07
	 * @param paperId 
	 * void
	 */
	List<PaperQuestion> getList(Integer paperId);
	
	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 chenyun 2022年2月15日上午9:56:07
	 * @param examId
	 * @param userId 
	 * void
	 */
	List<PaperQuestion> getList(Integer examId, Integer userId);

	/**
	 * 获取试卷试题列表
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:29:52
	 * @param questionId
	 * @return List<PaperQuestion>
	 */
	List<PaperQuestion> getList2(Integer questionId);
}
