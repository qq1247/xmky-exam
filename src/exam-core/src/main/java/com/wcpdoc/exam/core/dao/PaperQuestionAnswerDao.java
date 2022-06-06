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
	 * 获取试卷答案
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:54:30
	 * @param paperId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getList(Integer paperId);

	/**
	 * 获取试卷答案
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:54:30
	 * @param paperId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getList(Integer examId, Integer userId);

	/**
	 * 获取试卷答案列表
	 * 
	 * v1.0 zhanghc 2022年5月22日下午4:02:07
	 * @param paperId
	 * @param questionId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getListForSingleQuestion(Integer paperId, Integer questionId);

	/**
	 * 获取试卷答案列表
	 * 
	 * v1.0 zhanghc 2022年6月6日下午4:14:37
	 * @param chapterId
	 * @return List<PaperQuestionAnswer>
	 */
	List<PaperQuestionAnswer> getListByChapter(Integer chapterId);
}
