package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.entity.MyExam;

/**
 * 我的考试数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyExamDaoImpl extends RBaseDaoImpl<MyExam> implements MyExamDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM.ID AS EXAM_ID, EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, EXAM.END_TIME AS EXAM_END_TIME, "
				+ "EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME, EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME, EXAM.MARK_STATE AS EXAM_MARK_STATE, "
				+ "EXAM.SCORE_STATE AS EXAM_SCORE_STATE, EXAM.RANK_STATE AS EXAM_RANK_STATE, "
				+ "PAPER.ID AS PAPER_ID, PAPER.PASS_SCORE AS PAPER_PASS_SCORE, PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, PAPER.SHOW_TYPE AS PAPER_SHOW_TYPE, "
				+ "MARK_USER.ID AS MARK_USER_ID, MARK_USER.NAME AS MARK_USER_NAME, "
				+ "USER.ID AS USER_ID, USER.NAME AS USER_NAME, MY_EXAM.ANSWER_START_TIME, MY_EXAM.ANSWER_END_TIME, "
				+ "MY_EXAM.TOTAL_SCORE AS TOTAL_SCORE, MY_EXAM.STATE AS STATE, MY_EXAM.MARK_STATE AS MARK_STATE, "
				+ "MY_EXAM.ANSWER_STATE AS ANSWER_STATE, MY_EXAM.NO, (SELECT COUNT(1) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = MY_EXAM.EXAM_ID) AS USER_NUM "
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "LEFT JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_USER MARK_USER ON MY_EXAM.MARK_USER_ID = MARK_USER.ID ";// 阅卷用户不一定有
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examId")), "EXAM.ID = :EXAM_ID", pageIn.get("examId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("examName")), "EXAM.NAME LIKE :NAME", "%" + pageIn.get("examName") + "%")
				.addWhere(pageIn.get("curUserId", Integer.class) != null, "MY_EXAM.USER_ID = :USER_ID", pageIn.get("curUserId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("startTime")) && ValidateUtil.isValid(pageIn.get("endTime")), 
						"((EXAM.START_TIME <= :START_TIME1 AND :END_TIME1 >= EXAM.END_TIME) OR (EXAM.START_TIME <= :START_TIME2 AND :END_TIME2 >= EXAM.END_TIME) OR (EXAM.START_TIME >= :START_TIME3 AND EXAM.END_TIME >= :END_TIME3))", 
						pageIn.get("startTime"), pageIn.get("startTime"),
						pageIn.get("endTime"), pageIn.get("endTime"),
						pageIn.get("startTime"), pageIn.get("endTime")
						)
				.addWhere("PAPER.STATE = 1")
				.addWhere("EXAM.STATE = 1")
//				.addWhere("PAPER.STATE = 1")//删除了试卷也能查看
//				.addWhere("USER.STATE = 1")
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"examStartTime", DateUtil.FORMAT_DATE_TIME, 
				"examEndTime", DateUtil.FORMAT_DATE_TIME, 
				"examMarkEndTime", DateUtil.FORMAT_DATE_TIME, 
				"examMarkStartTime", DateUtil.FORMAT_DATE_TIME, 
				"answerStartTime", DateUtil.FORMAT_DATE_TIME, 
				"answerEndTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<MyExam> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_MY_EXAM WHERE EXAM_ID = :EXAM_ID";
		return getList(sql, new Object[] { examId }, MyExam.class);
	}
	@Override
	public List<MyExam> getListForUser(Integer userId) {
		String sql = "SELECT * FROM EXM_MY_EXAM WHERE USER_ID = :USER_ID";
		return getList(sql, new Object[] { userId }, MyExam.class);
	}

	@Override
	public MyExam getMyExam(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_MY_EXAM WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID";
		return getEntity(sql, new Object[] { examId, userId });
	}
}