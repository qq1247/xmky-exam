package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExamQuestionNo;

/**
 * 考试试题排序服务层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface ExamQuestionNoService extends BaseService<ExamQuestionNo>{
	
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
