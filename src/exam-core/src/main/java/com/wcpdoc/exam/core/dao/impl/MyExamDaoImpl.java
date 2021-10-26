package com.wcpdoc.exam.core.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.cache.DictCache;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
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
		String sql = "SELECT MY_EXAM.ID, EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, "
				+ "		MY_EXAM.ANSWER_START_TIME, MY_EXAM.ANSWER_END_TIME, "
				+ "		EXAM.END_TIME AS EXAM_END_TIME, PAPER.ID AS PAPER_ID, PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, "
				+ "		MY_EXAM.STATE AS STATE, MY_EXAM.TOTAL_SCORE AS TOTAL_SCORE, PAPER.SHOW_TYPE AS PAPER_SHOW_TYPE, "
				+ "		EXAM.MARK_START_TIME AS MARK_START_TIME, EXAM.MARK_END_TIME AS MARK_END_TIME, "
				+ "		USER.NAME AS USER_NAME, USER.ID AS USER_ID, MARK_USER.NAME AS MARK_USER_NAME, MARK_USER.ID AS MARK_USER_ID, "
				+ "		USER_UPDATE.ID AS UPDATE_USER_ID, USER_UPDATE.NAME AS UPDATE_USER_NAME,"
				+ "		MY_EXAM.MARK_STATE AS MARK_STATE, MY_EXAM.ANSWER_STATE AS ANSWER_STATE, "
				+ "		(SELECT COUNT(*) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = MY_EXAM.EXAM_ID) AS USER_NUM "
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "INNER JOIN SYS_USER USER_UPDATE ON MY_EXAM.UPDATE_USER_ID = USER_UPDATE.ID "
				+ "INNER JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_USER MARK_USER ON MY_EXAM.MARK_USER_ID = MARK_USER.ID ";// 阅卷用户不一定有
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(pageIn.get("examId", Integer.class) != null, "EXAM.ID = ?", pageIn.get("examId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("examName")), "EXAM.NAME LIKE ?", "%" + pageIn.get("examName") + "%")
				.addWhere(pageIn.get("examId", Integer.class) != null && pageIn.get("markUserId", Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_MY_MARK Z WHERE Z.EXAM_ID = ? AND Z.MARK_USER_ID = ? AND Z.EXAM_USER_IDS LIKE CONCAT('%,', MY_EXAM.USER_ID, ',%'))", pageIn.get("examId", Integer.class), pageIn.get("markUserId", Integer.class))
				.addWhere(pageIn.get("curUserId", Integer.class) != null, "MY_EXAM.USER_ID =  ?", pageIn.get("curUserId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("startDate")) && ValidateUtil.isValid(pageIn.get("endDate")), "(EXAM.START_TIME <= ? AND EXAM.START_TIME >= ?)", pageIn.get("endDate"), pageIn.get("startDate"))
				.addWhere("1".equals(pageIn.get("needExam")), "EXAM.START_TIME <= ? AND EXAM.END_TIME >= ?", new Date(), new Date())
				.addWhere("EXAM.STATE = ?", 1)
//				.addWhere("PAPER.STATE = ?", 1)//删除了试卷也能查看
//				.addWhere("USER.STATE = ?", 1)
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "examStartTime", DateUtil.FORMAT_DATE_TIME, "examEndTime", DateUtil.FORMAT_DATE_TIME, 
				"markEndTime", DateUtil.FORMAT_DATE_TIME, "markStartTime", DateUtil.FORMAT_DATE_TIME, 
				"answerStartTime", DateUtil.FORMAT_DATE_TIME, "answerEndTime", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), 
				"MY_EXAM_STATE", "state",
				"MY_EXAM_ANSWER_STATE", "answerState",
				"MY_EXAM_MARK_STATE", "markState"
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
	public MyExam getEntity(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_MY_EXAM WHERE EXAM_ID = ? AND USER_ID = ? ";
		return getEntity(sql, new Object[] { examId, userId });
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
		sqlUtil.addWhere(pageIn.get("examId", Integer.class) != null, "EXAM.ID = ?", pageIn.get("examId", Integer.class))
				.addOrder("MY_EXAM.TOTAL_SCORE", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}