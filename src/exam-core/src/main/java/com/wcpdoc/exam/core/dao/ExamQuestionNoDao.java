package com.wcpdoc.exam.core.dao;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.ExamQuestionNo;

/**
 * 考试试题排序数据访问层接口
 * 
 * v1.0 chenyun 2017-05-26 14:23:38
 */
public interface ExamQuestionNoDao extends BaseDao<ExamQuestionNo>{
	
	/**
	 * 获取考试试题排序
	 * 
	 * v1.0 zhanghc 2022年5月20日上午9:18:55
	 * @param examId
	 * @param userId
	 * @return ExamQuestionNo
	 */
	ExamQuestionNo getEntity(Integer examId, Integer userId);
}
