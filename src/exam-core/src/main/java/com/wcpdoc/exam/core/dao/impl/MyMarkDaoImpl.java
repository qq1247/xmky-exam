package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.cache.DictCache;
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
		String sql = "SELECT MY_MARK.ID, EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, EXAM.PAPER_ID AS PAPER_ID, PAPER.SHOW_TYPE AS PAPER_SHOW_TYPE, "
				+ "		EXAM.END_TIME AS EXAM_END_TIME, PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, MY_MARK.EXAM_USER_IDS AS EXAM_USER_IDS, "
				+ "		EXAM.STATE AS STATE, USER.NAME AS MARK_USER_NAME, USER.ID AS MARK_USER_ID, EXAM.ID AS EXAM_ID, UPDATE_USER.ID AS UPDATE_USER_ID, UPDATE_USER.NAME AS UPDATE_USER_NAME, "
				+ "		EXAM.MARK_START_TIME AS MARK_START_TIME, EXAM.MARK_END_TIME AS MARK_END_TIME, PAPER.PASS_SCORE AS PAPER_PASS_SCORE "
				+ "		FROM EXM_MY_MARK MY_MARK "
				+ "		INNER JOIN EXM_EXAM EXAM ON MY_MARK.EXAM_ID = EXAM.ID "
				+ "		INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "		INNER JOIN SYS_USER USER ON MY_MARK.MARK_USER_ID = USER.ID "
				+ "		INNER JOIN SYS_USER UPDATE_USER ON MY_MARK.UPDATE_USER_ID = UPDATE_USER.ID ";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examName")), "EXAM.NAME LIKE ?", "%" + pageIn.get("examName") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("userId")), "EXISTS (SELECT 1 FROM EXM_MY_MARK Z WHERE Z.MARK_USER_ID = ? AND Z.EXAM_ID = MY_MARK.EXAM_ID)", pageIn.get("userId"))
				.addWhere(pageIn.get("curUserId", Integer.class) != null, "MY_MARK.MARK_USER_ID =  ?", pageIn.get("curUserId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("startDate")) && ValidateUtil.isValid(pageIn.get("endDate")), "(EXAM.MARK_START_TIME <= ? AND EXAM.MARK_START_TIME >= ?)", pageIn.get("endDate"), pageIn.get("startDate"))
				.addWhere("EXAM.STATE = ?", 1)
				.addOrder("MY_MARK.ID", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "examStartTime", DateUtil.FORMAT_DATE_TIME, "examEndTime", DateUtil.FORMAT_DATE_TIME, 
				"markEndTime", DateUtil.FORMAT_DATE_TIME, "markStartTime", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), 
				"MY_EXAM_MARK_STATE", "state"
				);
		return pageOut;
	}

	@Override
	public List<MyMark> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_MY_MARK WHERE EXAM_ID = ?";
		return getList(sql, new Object[] { examId }, MyMark.class);
	}
}