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
	 * 获取考试试题列表
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:29:52
	 * @param examId
	 * @return List<ExamQuestion>
	 */
	List<ExamQuestion> getList(Integer examId);

	/**
	 * 清空试卷
	 * 
	 * v1.0 zhanghc 2023年3月23日上午11:03:15
	 * @param examId void
	 */
	void clear(Integer examId);
}
