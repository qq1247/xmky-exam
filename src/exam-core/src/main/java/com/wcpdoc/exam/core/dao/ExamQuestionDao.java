package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.ExamQuestion;

/**
 * 考试试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
public interface ExamQuestionDao extends RBaseDao<ExamQuestion> {

	/**
	 * 考试试题列表
	 * 
	 * v1.0 zhanghc 2022年5月25日下午5:29:52
	 * 
	 * @param examId
	 * @return List<ExamQuestion>
	 */
	default List<ExamQuestion> getList(Integer examId) {
		return selectList(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId, examId)
				.orderByAsc(ExamQuestion::getNo));
	}

	/**
	 * 试卷清理
	 * 
	 * v1.0 zhanghc 2023年3月23日上午11:03:15
	 * 
	 * @param examId void
	 */
	default void clear(Integer examId) {
		delete(new LambdaQueryWrapper<ExamQuestion>().eq(ExamQuestion::getExamId, examId));
	}
}
