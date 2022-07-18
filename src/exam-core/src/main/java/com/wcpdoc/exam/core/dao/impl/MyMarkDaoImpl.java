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
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.MyMark;

/**
 * 我的阅卷数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyMarkDaoImpl extends RBaseDaoImpl<MyMark> implements MyMarkDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM.ID AS EXAM_ID, EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, EXAM.END_TIME AS EXAM_END_TIME, "
				+ "EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME, EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME, EXAM.MARK_STATE AS EXAM_MARK_STATE, "
				+ "PAPER.ID AS PAPER_ID, PAPER.PASS_SCORE AS PAPER_PASS_SCORE, PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, PAPER.SHOW_TYPE AS PAPER_SHOW_TYPE, "
				+ "USER.ID AS MARK_USER_ID, USER.NAME AS MARK_USER_NAME "
				+ "FROM EXM_MY_MARK MY_MARK "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_MARK.EXAM_ID = EXAM.ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "INNER JOIN SYS_USER USER ON MY_MARK.MARK_USER_ID = USER.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examName")), "EXAM.NAME LIKE :NAME", "%" + pageIn.get("examName") + "%")
				.addWhere(pageIn.get("curUserId", Integer.class) != null, "MY_MARK.MARK_USER_ID = :MARK_USER_ID", pageIn.get("curUserId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("startTime")) && ValidateUtil.isValid(pageIn.get("endTime")), 
						"((EXAM.MARK_START_TIME <= :MARK_START_TIME1 AND :MARK_END_TIME1 >= EXAM.MARK_END_TIME) OR (EXAM.MARK_START_TIME <= :MARK_START_TIME2 AND :MARK_END_TIME2 >= EXAM.MARK_END_TIME) OR (EXAM.MARK_START_TIME >= :MARK_START_TIME3 AND EXAM.MARK_END_TIME >= :MARK_END_TIME3))",
						pageIn.get("startTime"), pageIn.get("startTime"),
						pageIn.get("endTime"), pageIn.get("endTime"),
						pageIn.get("startTime"), pageIn.get("endTime"))
				.addWhere("PAPER.STATE = :PAPER_STATE", 1)
				.addWhere("EXAM.STATE = :EXAM_STATE", 1)
				.addOrder("MY_MARK.ID", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"examStartTime", DateUtil.FORMAT_DATE_TIME, 
				"examEndTime", DateUtil.FORMAT_DATE_TIME, 
				"examMarkEndTime", DateUtil.FORMAT_DATE_TIME, 
				"examMarkStartTime", DateUtil.FORMAT_DATE_TIME, 
				"markEndTime", DateUtil.FORMAT_DATE_TIME, 
				"markStartTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<MyMark> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_MY_MARK WHERE EXAM_ID = :EXAM_ID";
		return getList(sql, new Object[] { examId }, MyMark.class);
	}

	@Override
	public List<MyMark> getListForMarUser(Integer markUserId) {
		String sql = "SELECT * FROM EXM_MY_MARK WHERE MARK_USER_ID = :MARK_USER_ID";
		return getList(sql, new Object[] { markUserId });
	}
}