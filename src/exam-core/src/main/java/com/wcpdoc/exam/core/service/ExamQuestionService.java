package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExamQuestion;

/**
 * 考试试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface ExamQuestionService extends BaseService<ExamQuestion> {
	/**
	 * 获取考试试题列表
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:28:58
	 * 
	 * @param examId
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getList(Integer examId);

	/**
	 * 试卷清理
	 * 
	 * v1.0 zhanghc 2023年3月23日上午11:03:15
	 * 
	 * @param examId void
	 */
	void clear(Integer examId);
}
