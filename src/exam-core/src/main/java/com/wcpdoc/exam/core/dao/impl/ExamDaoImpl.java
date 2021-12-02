package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.cache.DictCache;
import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;

/**
 * 考试数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Repository
public class ExamDaoImpl extends RBaseDaoImpl<Exam> implements ExamDao {
	@Resource
	private UserDao userDao;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM.ID, EXAM.NAME, EXAM.START_TIME, EXAM.END_TIME, "
				+ "EXAM.MARK_START_TIME, EXAM.MARK_END_TIME, EXAM.STATE, PAPER.MARK_TYPE AS PAPER_MARK_TYPE, "
				+ "PAPER.TOTAL_SCORE AS PAPER_TOTLE_SCORE, PAPER.ID AS PAPER_ID, PAPER.NAME AS PAPER_NAME, "
				+ "PAPER.PASS_SCORE AS PAPER_PASS_SCORE, UPDATE_USER.NAME AS UPDATE_USER_NAME "
				+ "FROM EXM_EXAM EXAM "
				+ "LEFT JOIN EXM_EXAM_TYPE EXAM_TYPE ON EXAM.EXAM_TYPE_ID = EXAM_TYPE.ID "
				+ "LEFT JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "LEFT JOIN SYS_USER UPDATE_USER ON EXAM.UPDATE_USER_ID = UPDATE_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examTypeId")), "EXAM.EXAM_TYPE_ID = ?", pageIn.get("examTypeId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("name")), "EXAM.NAME LIKE ?", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "EXAM.UPDATE_USER_ID = ?", pageIn.get("curUserId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("markState")), "EXAM.MARK_STATE = ?", pageIn.get("markState", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "EXAM.STATE = ?", pageIn.get("state", Integer.class))
				.addWhere(!ValidateUtil.isValid(pageIn.get("state")), "EXAM.STATE IN (1,2)")
				.addOrder("EXAM.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"startTime", DateUtil.FORMAT_DATE_TIME,
				"endTime", DateUtil.FORMAT_DATE_TIME,
				"markStartTime", DateUtil.FORMAT_DATE_TIME,
				"markEndTime", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), "EXAM_STATE", "state");
		return pageOut;
	}
	
	@Override
	public List<Exam> getList(Integer examTypeId) {
		String sql = "SELECT * FROM EXM_EXAM EXAM_TYPE WHERE STATE !=0 AND EXAM_TYPE_ID = ?";
		return getList(sql, new Object[]{examTypeId}, Exam.class);
	}

	@Override
	public PageOut getGradeListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<Exam> getExamList(Integer paperId) {
		String sql = "SELECT * FROM EXM_EXAM EXAM_TYPE WHERE STATE = 1 AND PAPER_ID = ?";
		return getList(sql, new Object[]{paperId}, Exam.class);
	}
	
	@Override
	public List<Map<String, Object>> getExamUserList(Integer id) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE EXISTS (SELECT 1 FROM EXM_MY_EXAM Z WHERE Z.EXAM_ID = ? AND USER.ID = Z.USER_ID) "
				+ "ORDER BY USER.UPDATE_TIME DESC ";//USER.STATE = 1 用户被删除也应该显示
		return getMapList(sql, new Object[]{id});
	}

	@Override
	public List<Map<String, Object>> getMarkExamUserList(Integer id, Integer markUserId) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE EXISTS (SELECT 1 FROM EXM_MY_MARK Z WHERE Z.EXAM_ID = ? AND Z.MARK_USER_ID = ? AND Z.EXAM_USER_IDS LIKE CONCAT( '%,' , USER.ID , ',%' )) "//回显的情况下，用户状态!=1的也查询
				+ "ORDER BY USER.UPDATE_TIME DESC ";
		return getMapList(sql, new Object[]{id, markUserId});
	}

	@Override
	public List<Map<String, Object>> getMarkQuestionList(Integer id, Integer markUserId) {
		String sql = "SELECT QUESTION.ID, QUESTION.TITLE "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE EXISTS (SELECT 1 FROM EXM_MY_MARK Z WHERE Z.EXAM_ID = ? AND Z.MARK_USER_ID = ? AND Z.QUESTION_IDS LIKE CONCAT( '%,' , QUESTION.ID , ',%' )) "//回显的情况下，试题状态!=1的也查询
				+ "ORDER BY QUESTION.UPDATE_TIME DESC ";
		return getMapList(sql, new Object[]{id, markUserId});
	}

}