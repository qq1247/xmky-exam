package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.ExamQuestionDao;
import com.wcpdoc.exam.core.entity.ExamQuestion;

/**
 * 考试试题数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Repository
public class ExamQuestionDaoImpl extends RBaseDaoImpl<ExamQuestion> implements ExamQuestionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<ExamQuestion> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_EXAM_QUESTION WHERE EXAM_ID = :EXAM_ID ORDER BY NO ASC";
		return getList(sql, new Object[] { examId }, ExamQuestion.class);
	}
}