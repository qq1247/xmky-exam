package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.exam.core.entity.ExamUserQuestion;
import com.wcpdoc.exam.core.service.BaseService;
/**
 * 考试用户试题服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface ExamUserQuestionService extends BaseService<ExamUserQuestion>{

	/**
	 * 获取考试用户试题列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param examUserId
	 * @return List<ExamUserQuestion>
	 */
	List<ExamUserQuestion> getList(Integer examUserId);

	/**
	 * 获取考试用户试题列表
	 * 
	 * v1.0 zhanghc 2017年8月14日下午3:51:27
	 * @param examId
	 * @param userId
	 * @return List<ExamUserQuestion>
	 */
	List<ExamUserQuestion> getList(Integer examId, Integer userId);
}
