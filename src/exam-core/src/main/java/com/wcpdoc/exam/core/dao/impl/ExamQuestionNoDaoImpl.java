package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.ExamQuestionNoDao;
import com.wcpdoc.exam.core.entity.ExamQuestionNo;

/**
 * 考试试题排序数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Repository
public class ExamQuestionNoDaoImpl extends RBaseDaoImpl<ExamQuestionNo> implements ExamQuestionNoDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public ExamQuestionNo getEntity(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_EXAM_QUESTION_NO WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID";
		return getEntity(sql, new Object[] { examId, userId }, ExamQuestionNo.class);
	}
}