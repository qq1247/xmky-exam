package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.MyExamDetailDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

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
	public List<MyExamDetail> getList(Integer myExamId) {
		String sql = "SELECT * FROM EXM_MY_EXAM_DETAIL WHERE MY_EXAM_ID = ?";
		return getList(sql, new Object[] { myExamId }, MyExamDetail.class);
	}

	@Override
	public void delByMyExamId(Integer myExamId) {
		String sql = "DELETE FROM EXM_MY_EXAM_DETAIL WHERE MY_EXAM_ID = ?";
		update(sql, new Object[] { myExamId });
	}

	@Override
	public List<Map<String, Object>> getAnswerList(Integer myExamId, Integer curUserId) {
		String sql = "SELECT MY_EXAM_DETAIL.*, USER.NAME AS MARK_USER_NAME, QUESTION.TYPE AS QUESTION_TYPE "
					+" FROM EXM_MY_EXAM_DETAIL MY_EXAM_DETAIL "
					+" LEFT JOIN EXM_QUESTION QUESTION ON MY_EXAM_DETAIL.QUESTION_ID = QUESTION.ID "// 根据试题类型进行答案分隔
					+" LEFT JOIN SYS_USER USER ON MY_EXAM_DETAIL.MARK_USER_ID = USER.ID "
					+" WHERE MY_EXAM_DETAIL.MY_EXAM_ID = ? AND MY_EXAM_DETAIL.USER_ID = ? ";
		return getMapList(sql, new Object[] { myExamId, curUserId });
	}

	@Override
	public List<Map<String, Object>> getMarkAnswerList(Integer userId, Integer examId) {
		String sql = "SELECT MY_EXAM_DETAIL.*, USER.NAME AS MARK_USER_NAME, QUESTION.TYPE AS QUESTION_TYPE "
				+" FROM EXM_MY_EXAM_DETAIL MY_EXAM_DETAIL "
				+" LEFT JOIN EXM_QUESTION QUESTION ON MY_EXAM_DETAIL.QUESTION_ID = QUESTION.ID "
				+" LEFT JOIN SYS_USER USER ON MY_EXAM_DETAIL.MARK_USER_ID = USER.ID "
				+" WHERE MY_EXAM_DETAIL.USER_ID = ? AND MY_EXAM_DETAIL.EXAM_ID = ? ";
	return getMapList(sql, new Object[] { userId, examId });
	}
}