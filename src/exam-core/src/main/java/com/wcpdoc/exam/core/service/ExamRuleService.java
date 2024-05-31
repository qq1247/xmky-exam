package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExamRule;

/**
 * 考试规则服务层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface ExamRuleService extends BaseService<ExamRule> {

	/**
	 * 考试规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:30:01
	 * 
	 * @param examId
	 * @return List<ExamRule>
	 */
	List<ExamRule> getList(Integer examId);

	/**
	 * 考试规则清理
	 * 
	 * v1.0 zhanghc 2023年3月23日上午11:05:07
	 * 
	 * @param examId void
	 */
	void clear(Integer examId);
}
