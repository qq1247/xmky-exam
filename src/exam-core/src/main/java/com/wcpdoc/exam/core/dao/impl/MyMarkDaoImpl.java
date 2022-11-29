package com.wcpdoc.exam.core.dao.impl;

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

/**
 * 我的阅卷数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyMarkDaoImpl extends RBaseDaoImpl<Object> implements MyMarkDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM.ID AS EXAM_ID, EXAM.NAME AS EXAM_NAME, "// 考试相关字段
				+ "EXAM.START_TIME AS EXAM_START_TIME, EXAM.END_TIME AS EXAM_END_TIME, "// 考试相关字段
				+ "EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME, EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME, "// 考试相关字段
				+ "EXAM.MARK_STATE AS EXAM_MARK_STATE, EXAM.PASS_SCORE AS EXAM_PASS_SCORE, EXAM.TOTAL_SCORE AS EXAM_TOTAL_SCORE "// 考试相关字段
				+ "FROM EXM_EXAM EXAM ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examName")), "EXAM.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("examName")))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), 
						"EXISTS (SELECT 1 FROM EXM_MY_EXAM Z WHERE Z.MARK_USER_ID = :MARK_USER_ID AND EXAM.ID = Z.EXAM_ID)", 
						pageIn.get("curUserId", Integer.class))
				.addWhere("EXAM.STATE = :EXAM_STATE", 1)
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"examStartTime", DateUtil.FORMAT_DATE_TIME, 
				"examEndTime", DateUtil.FORMAT_DATE_TIME, 
				"examMarkEndTime", DateUtil.FORMAT_DATE_TIME, 
				"examMarkStartTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID AS USER_ID, USER.NAME AS USER_NAME, "// 用户
				+ "ORG.ID AS ORG_ID, ORG.NAME AS ORG_NAME, " // 机构
				+ "MY_EXAM.STATE, MY_EXAM.MARK_STATE, "// 我的考试
				+ "MY_EXAM.OBJECTIVE_SCORE, MY_EXAM.TOTAL_SCORE, MY_EXAM.ANSWER_STATE AS ANSWER_STATE "// 我的考试
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "LEFT JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examId")), "MY_EXAM.EXAM_ID = :EXAM_ID",pageIn.get("examId"))
			.addWhere(ValidateUtil.isValid(pageIn.get("userName")), "USER.NAME LIKE :USER_NAME", String.format("%%%s%%", pageIn.get("userName")))
			.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "MY_EXAM.MARK_USER_ID = :MARK_USER_ID", pageIn.get("curUserId", Integer.class))
			.addWhere("EXAM.STATE = :EXAM_STATE", 1)
			.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}