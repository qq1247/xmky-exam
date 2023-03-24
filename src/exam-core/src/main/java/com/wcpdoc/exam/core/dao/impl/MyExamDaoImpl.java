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
import com.wcpdoc.exam.core.entity.Exam;
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
		String sql = "SELECT EXAM.ID AS EXAM_ID, EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, EXAM.END_TIME AS EXAM_END_TIME, "// 考试相关字段
				+ "EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME, EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME, EXAM.MARK_STATE AS EXAM_MARK_STATE, "// 考试相关字段
				+ "EXAM.SCORE_STATE AS EXAM_SCORE_STATE, EXAM.RANK_STATE AS EXAM_RANK_STATE, EXAM.PASS_SCORE AS EXAM_PASS_SCORE, "// 考试相关字段
				+ "EXAM.TOTAL_SCORE AS EXAM_TOTAL_SCORE, "// 考试相关字段
				+ "USER.ID AS USER_ID, USER.NAME AS USER_NAME, "// 考试用户字段
				+ "MARK_USER.ID AS MARK_USER_ID, MARK_USER.NAME AS MARK_USER_NAME, "// 阅卷用户字段
				+ "MY_EXAM.ANSWER_START_TIME, MY_EXAM.ANSWER_END_TIME, MY_EXAM.TOTAL_SCORE AS TOTAL_SCORE, MY_EXAM.STATE AS STATE, "// 我的考试字段
				+ "MY_EXAM.MARK_STATE AS MARK_STATE, MY_EXAM.ANSWER_STATE AS ANSWER_STATE, MY_EXAM.NO, "// 我的考试字段
				+ "(SELECT COUNT(1) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = MY_EXAM.EXAM_ID) AS USER_NUM "// 用户数量（排名使用）
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "LEFT JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_USER MARK_USER ON MY_EXAM.MARK_USER_ID = MARK_USER.ID ";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examName")), "EXAM.NAME LIKE :NAME", "%" + pageIn.get("examName") + "%")
				.addWhere(pageIn.get("curUserId", Integer.class) != null, "MY_EXAM.USER_ID = :USER_ID", pageIn.get("curUserId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("startTime")) && ValidateUtil.isValid(pageIn.get("endTime")), 
						"(( :START_TIME1 <= EXAM.START_TIME AND EXAM.START_TIME <= :END_TIME1) "
						+ "	OR ( :START_TIME2 <= EXAM.END_TIME AND EXAM.END_TIME <= :END_TIME2) "
						+ "	OR ( :START_TIME3 >= EXAM.START_TIME AND EXAM.END_TIME >= :END_TIME3))", 
						pageIn.get("startTime"), pageIn.get("endTime"),
						pageIn.get("startTime"), pageIn.get("endTime"),
						pageIn.get("startTime"), pageIn.get("endTime")
						)
				.addWhere("EXAM.STATE = 1")// 已发布（不含冻结）
				.addOrder("EXAM.START_TIME", Order.DESC);// 按考试开始时间倒序排列
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

	@Override
	public List<Exam> getExamList(Integer userId) {
		String sql = "SELECT EXAM.* "
				+ "FROM EXM_EXAM EXAM "
				+ "WHERE EXAM.STATE != 0 "
				+ "	AND EXISTS (SELECT 1 FROM EXM_MY_EXAM Z WHERE Z.USER_ID = :USER_ID AND EXAM.ID = Z.EXAM_ID) ";
		return getList(sql, new Object[] { userId }, Exam.class);
	}

	@Override
	public void clear(Integer examId) {
		String sql = "DELETE FROM EXM_MY_EXAM WHERE EXAM_ID = :EXAM_ID";
		update(sql, new Object[] { examId });
	}
}