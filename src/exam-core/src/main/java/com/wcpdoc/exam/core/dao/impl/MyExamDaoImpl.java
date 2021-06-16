package com.wcpdoc.exam.core.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 我的考试数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyExamDaoImpl extends RBaseDaoImpl<MyExam> implements MyExamDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT MY_EXAM.ID, EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, "
				+ "		EXAM.END_TIME AS EXAM_END_TIME, PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, "
				+ "		MY_EXAM.STATE AS MY_EXAM_STATE, MY_EXAM.TOTAL_SCORE AS MY_EXAM_TOTAL_SCORE, "
				+ "		EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME, EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME, USER.NAME AS USER_NAME, "
				+ "		MY_EXAM.MARK_STATE AS MY_EXAM_MARK_STATE, MY_EXAM.ANSWER_STATE AS MY_EXAM_ANSWER_STATE, "
				+ "		(SELECT COUNT(*) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = MY_EXAM.EXAM_ID) AS USER_NUM "
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "INNER JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()), "EXAM.ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "EXAM.NAME LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getEight()), "EXISTS (SELECT 1 FROM EXM_MY_MARK Z WHERE USER_ID = ? AND Z.EXAM_ID = MY_EXAM.EXAM_ID)", pageIn.getEight())
				.addWhere(ValidateUtil.isValid(pageIn.getNine()), "MY_EXAM.MARK_USER_ID =  ?", pageIn.getNine())
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "MY_EXAM.USER_ID =  ?", pageIn.getTen())
				.addWhere("EXAM.STATE = ?", 1)
//				.addWhere("PAPER.STATE = ?", 1)//删除了试卷也能查看
//				.addWhere("USER.STATE = ?", 1)
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "EXAM_START_TIME", DateUtil.FORMAT_DATE_TIME, "EXAM_END_TIME", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), 
				"MY_EXAM_STATE", "MY_EXAM_STATE",
				"MY_EXAM_ANSWER_STATE", "MY_EXAM_ANSWER_STATE",
				"MY_EXAM_MARK_STATE", "MY_EXAM_MARK_STATE"
				);
		return pageOut;
	}

	@Override
	public void del(Integer roomId, Integer userId) {
		String sql = "DELETE FROM EXM_MY_EXAM WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, new Object[]{roomId, userId});
	}

	@Override
	public List<MyExam> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_MY_EXAM WHERE EXAM_ID = ?";
		return getList(sql, new Object[] { examId }, MyExam.class);
	}

	@Override
	public List<MyExam> kalendar(Integer userId, Date startTime, Date endTime) {
		String sql = "SELECT * FROM EXM_MY_EXAM WHERE USER_ID = ? AND ANSWER_TIME >= ? AND ANSWER_TIME <= ? ";
 		return getList(sql, new Object[] { userId, startTime, endTime });
	}

	@Override
	public PageOut getRankingPage(PageIn pageIn) {
		String sql = "SELECT MY_EXAM.ID, MY_EXAM.TOTAL_SCORE, MY_EXAM.EXAM_ID, MY_EXAM.USER_ID, EXAM.NAME AS EXAM_NAME, USER.NAME AS USER_NAME "
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "INNER JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()), "EXAM.ID = ?", pageIn.getOne())
				.addOrder("MY_EXAM.TOTAL_SCORE", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> count(Integer examId) {
		String sql = "SELECT MAX( MY_EXAM.TOTAL_SCORE ) AS MAX, MIN( MY_EXAM.TOTAL_SCORE ) AS MIN, AVG( MY_EXAM.TOTAL_SCORE ) AS AVG, "
				+ "SUM( MY_EXAM.ANSWER_STATE = 1 ) AS ANSWER, EXM_EXAM.START_TIME AS START_TIME, EXM_EXAM.END_TIME AS END_TIME, "
				+ "EXM_PAPER.TOTAL_SCORE AS TOTAL_SCORE FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM ON EXM_EXAM.ID =  MY_EXAM.EXAM_ID "
				+ "INNER JOIN EXM_PAPER ON EXM_PAPER.ID = EXM_EXAM.PAPER_ID "
				+ "WHERE MY_EXAM.EXAM_ID = ?";
 		return getMapList(sql, new Object[] { examId });
	}
}