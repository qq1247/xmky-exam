package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.dao.ExamUserDao;
import com.wcpdoc.exam.core.entity.ExamUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 考试用户数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class ExamUserDaoImpl extends RBaseDaoImpl<ExamUser> implements ExamUserDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_USER.ID, EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, "
				+ "		EXAM.END_TIME AS EXAM_END_TIME, PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, EXAM.PASS_SCORE AS EXAM_PASS_SCORE, "
				+ "		EXAM_USER.STATE AS EXAM_USER_STATE, EXAM_USER.TOTAL_SCORE AS EXAM_USER_TOTAL_SCORE, "
				+ "		EXAM.MARK_START_TIME AS EXAM_MARK_START_TIME, EXAM.MARK_END_TIME AS EXAM_MARK_END_TIME, USER.NAME AS USER_NAME, "
				+ "		(SELECT COUNT(*) FROM EXM_EXAM_USER A WHERE A.EXAM_ID = EXAM_USER.EXAM_ID) AS USER_NUM "
				+ "FROM EXM_EXAM_USER EXAM_USER "
				+ "INNER JOIN EXM_EXAM EXAM ON EXAM_USER.EXAM_ID = EXAM.ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "INNER JOIN SYS_USER USER ON EXAM_USER.USER_ID = USER.ID";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXAM_USER.USER_ID =  ?", pageIn.getTen())
				.addWhere(ValidateUtil.isValid(pageIn.getNine()), "EXAM_USER.MARK_USER_ID =  ?", pageIn.getNine())
				.addWhere(ValidateUtil.isValid(pageIn.getEight()), "EXISTS (SELECT 1 FROM EXM_MARK_USER Z WHERE USER_ID = ? AND Z.EXAM_ID = EXAM_USER.EXAM_ID)", pageIn.getEight())
				.addWhere("EXAM.STATE = ?", 1)
//				.addWhere("PAPER.STATE = ?", 1)//删除了试卷也能查看
//				.addWhere("USER.STATE = ?", 1)
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getRows(), "EXAM_START_TIME", DateUtil.FORMAT_DATE_TIME, "EXAM_END_TIME", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "EXAM_USER_STATE", "EXAM_USER_STATE");
		return pageOut;
	}

	@Override
	public void del(Integer roomId, Integer userId) {
		String sql = "DELETE FROM EXM_EXAM_USER WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, new Object[]{roomId, userId});
	}

	@Override
	public List<ExamUser> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_EXAM_USER WHERE EXAM_ID = ?";
		return getList(sql, new Object[] { examId }, ExamUser.class);
	}
}