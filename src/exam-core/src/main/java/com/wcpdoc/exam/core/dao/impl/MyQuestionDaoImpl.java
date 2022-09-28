package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

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
		String sql = "SELECT * FROM EXM_MY_QUESTION WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID";
		return getList(sql, new Object[] { examId, userId }, MyQuestion.class);
	}

	@Override
	public MyQuestion getMyQuestion(Integer examId, Integer userId, Integer questionId) {
		String sql = "SELECT * FROM EXM_MY_QUESTION WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID AND QUESTION_ID = :QUESTION_ID";
		return getEntity(sql, new Object[] { examId, userId, questionId });
	}

	@Override
	public List<Map<String, Object>> getAnswerList(Integer examId, Integer userId) {
		String sql = "SELECT MY_QUESTION.QUESTION_ID, MY_QUESTION.ANSWER_TIME, "
				+ "MY_QUESTION.ANSWER, MY_QUESTION.MARK_USER_ID, MARK_USER.NAME AS MARK_USER_NAME, "
				+ "MY_QUESTION.MARK_TIME, MY_QUESTION.SCORE, MY_QUESTION.QUESTION_SCORE, "
				+ "MY_QUESTION.ANSWER_FILE_ID, QUESTION.TYPE AS QUESTION_TYPE, QUESTION.MARK_TYPE AS QUESTION_MARK_TYPE "
				+ "FROM EXM_MY_QUESTION MY_QUESTION "
				+ "LEFT JOIN EXM_QUESTION QUESTION ON MY_QUESTION.QUESTION_ID = QUESTION.ID "
				+ "LEFT JOIN SYS_USER MARK_USER ON MY_QUESTION.MARK_USER_ID = MARK_USER.ID "
				+" WHERE MY_QUESTION.EXAM_ID = :EXAM_ID AND MY_QUESTION.USER_ID = :USER_ID";
		return getMapList(sql, new Object[] { examId, userId });
	}
}