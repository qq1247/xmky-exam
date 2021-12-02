package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.MyExamDetailDao;
import com.wcpdoc.exam.core.entity.MyExamDetail;

/**
 * 我的考试详细数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyExamDetailDaoImpl extends RBaseDaoImpl<MyExamDetail> implements MyExamDetailDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<MyExamDetail> getList(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_MY_EXAM_DETAIL WHERE EXAM_ID = ? AND USER_ID = ?";
		return getList(sql, new Object[] { examId, userId }, MyExamDetail.class);
	}

	@Override
	public List<Map<String, Object>> getAnswerList(Integer examId, Integer userId) {
		String sql = "SELECT MY_EXAM_DETAIL.QUESTION_ID, MY_EXAM_DETAIL.ANSWER_TIME, "
				+ "MY_EXAM_DETAIL.ANSWER, MY_EXAM_DETAIL.MARK_USER_ID, MARK_USER.NAME AS MARK_USER_NAME, "
				+ "MY_EXAM_DETAIL.MARK_TIME, MY_EXAM_DETAIL.SCORE, MY_EXAM_DETAIL.QUESTION_SCORE, "
				+ "MY_EXAM_DETAIL.ANSWER_FILE_ID, QUESTION.TYPE AS QUESTION_TYPE, QUESTION.AI AS QUESTION_AI "
				+ "FROM EXM_MY_EXAM_DETAIL MY_EXAM_DETAIL "
				+ "LEFT JOIN SYS_USER MARK_USER ON MY_EXAM_DETAIL.MARK_USER_ID = MARK_USER.ID "
				+ "LEFT JOIN EXM_QUESTION QUESTION ON QUESTION.ID = MY_EXAM_DETAIL.QUESTION_ID"
				+" WHERE MY_EXAM_DETAIL.EXAM_ID = ? AND MY_EXAM_DETAIL.USER_ID = ? ";
		return getMapList(sql, new Object[] { examId, userId });
	}

	@Override
	public List<Map<String, Object>> getMarkAnswerList(Integer userId, Integer examId) {
		String sql = "SELECT MY_EXAM_DETAIL.QUESTION_ID, MY_EXAM_DETAIL.ANSWER_TIME, "
				+ "MY_EXAM_DETAIL.ANSWER, MY_EXAM_DETAIL.MARK_USER_ID, MARK_USER.NAME AS MARK_USER_NAME, "
				+ "MY_EXAM_DETAIL.MARK_TIME, MY_EXAM_DETAIL.SCORE, MY_EXAM_DETAIL.QUESTION_SCORE, "
				+ "MY_EXAM_DETAIL.ANSWER_FILE_ID, QUESTION.TYPE AS QUESTION_TYPE, QUESTION.AI AS QUESTION_AI "
				+"FROM EXM_MY_EXAM_DETAIL MY_EXAM_DETAIL "
				+"LEFT JOIN EXM_QUESTION QUESTION ON MY_EXAM_DETAIL.QUESTION_ID = QUESTION.ID "
				+"LEFT JOIN SYS_USER MARK_USER ON MY_EXAM_DETAIL.MARK_USER_ID = MARK_USER.ID "
				+"WHERE MY_EXAM_DETAIL.USER_ID = ? AND MY_EXAM_DETAIL.EXAM_ID = ? ";
	return getMapList(sql, new Object[] { userId, examId });
	}

	@Override
	public MyExamDetail getEntity(Integer examId, Integer userId, Integer questionId) {
		String sql = "SELECT * FROM EXM_MY_EXAM_DETAIL WHERE EXAM_ID = ? AND USER_ID = ? AND QUESTION_ID = ?";
		return getEntity(sql, new Object[] { examId, userId, questionId });
	}

	@Override
	public void del(Integer examId, Integer userId) {
		String sql = "DELETE FROM EXM_MY_EXAM_DETAIL WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, new Object[] { examId, userId });
	}
}