package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.ExamQuestion;

/**
 * 考试试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface ExamQuestionDao extends BaseDao<ExamQuestion>{
	/**
	 * 获取章节列表
	 * 
	 * v1.0 zhanghc 2018年10月21日下午2:43:46
	 * @param examId
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getChapterList(Integer examId);

	/**
	 * 获取章节详细列表
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:58:11
	 * @param examId
	 * @param no
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getChapterDetailList(Integer examId, Integer no);

	/**
	 * 获取考试试题实体
	 * 
	 * v1.0 zhanghc 2022年5月24日上午10:07:36
	 * @param examId
	 * @param questionId
	 * @return ExamQuestion
	 */
	ExamQuestion getEntity(Integer examId, Integer questionId);
	
	/**
	 * 获取考试试题实体
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:22:35
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return ExamQuestion
	 */
	ExamQuestion getEntity(Integer examId, Integer userId, Integer questionId);
	
	/**
	 * 获取考试试题列表
	 * 
	 * v1.0 chenyun 2022年2月15日上午9:56:07
	 * @param examId
	 * @param userId 
	 * void
	 */
	List<ExamQuestion> getList(Integer examId, Integer userId);

	/**
	 * 获取考试试题列表
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:29:52
	 * @param questionId
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getList(Integer questionId);
}
