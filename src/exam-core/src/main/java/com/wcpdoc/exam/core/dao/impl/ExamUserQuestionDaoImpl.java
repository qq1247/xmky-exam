package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.ExamUserQuestionDao;
import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.ExamUserQuestion;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 考试用户试题数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class ExamUserQuestionDaoImpl extends BaseDaoImpl<ExamUserQuestion> implements ExamUserQuestionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<ExamUserQuestion> getList(Integer examUserId) {
		String sql = "SELECT * FROM EXM_EXAM_USER_QUESTION WHERE EXAM_USER_ID = ?";
		return getList(sql, new Object[]{examUserId}, ExamUserQuestion.class);
	}

	@Override
	public List<ExamUserQuestion> getList(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_EXAM_USER_QUESTION WHERE EXAM_ID = ? AND USER_ID = ?";
		return getList(sql, new Object[]{examId, userId}, ExamUserQuestion.class);
	}
}