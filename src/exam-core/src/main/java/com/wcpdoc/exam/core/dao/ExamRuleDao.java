package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.ExamRule;

/**
 * 考试规则数据访问层接口
 * 
 * v1.0 chenyun 2022年2月11日 10:49:52
 */
public interface ExamRuleDao extends RBaseDao<ExamRule> {

	/**
	 * 考试规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:30:01
	 * 
	 * @param examId
	 * @return List<ExamRule>
	 */
	default List<ExamRule> getList(Integer examId) {
		return selectList(
				new LambdaQueryWrapper<ExamRule>().eq(ExamRule::getExamId, examId).orderByAsc(ExamRule::getNo));
	}

	/**
	 * 考试规则清理
	 * 
	 * v1.0 zhanghc 2023年3月23日上午11:05:07
	 * 
	 * @param examId void
	 */
	default void clear(Integer examId) {
		delete(new LambdaQueryWrapper<ExamRule>().eq(ExamRule::getExamId, examId));
	}
}