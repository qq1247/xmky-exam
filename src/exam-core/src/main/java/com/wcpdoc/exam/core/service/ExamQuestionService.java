package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExamQuestion;

/**
 * 考试试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface ExamQuestionService extends BaseService<ExamQuestion>{
	
	/**
	 * 获取章节列表
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:43:46
	 * @param examId
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getChapterList(Integer examId);
	
	/**
	 * 获取试卷详细列表
	 * 
	 * v1.0 zhanghc 2017年5月27日上午10:51:44
	 * @param examId
	 * @param no
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getChapterDetailList(Integer examId, Integer no);

	/**
	 * 获取考试试题列表
	 * 
	 * v1.0 zhanghc 2022年5月24日上午10:06:53
	 * @param examId
	 * @param questionId
	 * @return ExamQuestion
	 */
	ExamQuestion getEntity(Integer examId, Integer questionId);
	
	/**
	 * 获取考试试题列表
	 * 
	 * v1.0 chenyun 2022年2月15日上午9:56:07
	 * @param userId
	 * @param examId void
	 */
	List<ExamQuestion> getList(Integer examId, Integer userId);

	/**
	 * 获取考试试题实体
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:29:05
	 * @param id
	 * @param userId
	 * @param questionId
	 * @return ExamQuestion
	 */
	ExamQuestion getEntity(Integer id, Integer userId, Integer questionId);

	/**
	 * 获取考试试题列表
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:28:58
	 * @param questionId
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getList(Integer questionId);
}
