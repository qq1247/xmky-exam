package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.MyQuestionDao;
import com.wcpdoc.exam.core.entity.MyQuestion;

/**
 * 我的试题数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyQuestionDaoImpl extends RBaseDaoImpl<MyQuestion> implements MyQuestionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<MyQuestion> getList(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_MY_QUESTION WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID ORDER BY NO";
		return getList(sql, new Object[] { examId, userId }, MyQuestion.class);
	}

	@Override
	public MyQuestion getMyQuestion(Integer examId, Integer userId, Integer questionId) {
		String sql = "SELECT * FROM EXM_MY_QUESTION WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID AND QUESTION_ID = :QUESTION_ID";
		return getEntity(sql, new Object[] { examId, userId, questionId });
	}

	@Override
	public void clear(Integer examId) {
		String sql = "DELETE FROM EXM_MY_QUESTION WHERE EXAM_ID = :EXAM_ID";
		update(sql, new Object[] { examId });
	}
}