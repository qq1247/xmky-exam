package com.wcpdoc.exam.report.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.report.dao.ReportDao;

/**
 * 统计数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class ReportDaoImpl extends RBaseDaoImpl<Object> implements ReportDao {
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<Map<String, Object>> homeUser(Integer userId) {
		String sql = "SELECT USER.ID AS USER_ID, USER.NAME AS USER_NAME, USER.HEAD_FILE_ID AS USER_HEAD_FILE_ID, USER.ORG_ID AS ORG_ID, ORG.NAME AS ORG_NAME, USER.TYPE AS TYPE, "
				+ "EXAM.STATE AS EXAM_STATE, EXAM.START_TIME AS EXAM_START_TIME, EXAM.SCORE_STATE AS EXAM_SCORE_STATE, EXAM.RANK_STATE AS EXAM_RANK_STATE, "
				+ "MY_EXAM.STATE AS MY_EXAM_STATE, MY_EXAM.ANSWER_STATE AS MY_EXAM_ANSWER_STATE, MY_EXAM.NO AS MY_EXAM_NO, MY_EXAM.TOTAL_SCORE AS MY_EXAM_TOTAL_SCORE "
				+ "FROM EXM_MY_EXAM MY_EXAM INNER JOIN EXM_EXAM AS EXAM ON MY_EXAM.EXAM_ID = EXAM.ID INNER JOIN SYS_USER AS USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_ORG AS ORG ON USER.ORG_ID = ORG.ID WHERE MY_EXAM.STATE != 0 AND MY_EXAM.USER_ID = :USER_ID";
 		return getMapList(sql, new Object[] { userId });
	}

	@Override
	public Integer homeUserMissNum(Integer userId) {
		String sql = "SELECT COUNT(MY_EXAM.ID) "
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM AS EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "WHERE MY_EXAM.STATE = 1 AND EXAM.START_TIME < NOW() AND MY_EXAM.USER_ID = :USER_ID";
 		return getCount(sql, new Object[] { userId });
	}
	
	@Override
	public List<Map<String, Object>> homeSubAdminExam(Integer userId) {
		String sql = "SELECT USER.ID AS USER_ID, USER.NAME AS USER_NAME, USER.HEAD_FILE_ID AS USER_HEAD_FILE_ID, USER.ORG_ID AS ORG_ID ,ORG.NAME AS ORG_NAME, "
				+ "USER.TYPE AS TYPE, COUNT(EXAM.ID) AS EXAM_NUM "
				+ "FROM EXM_EXAM EXAM "
				+ "INNER JOIN SYS_USER AS USER ON EXAM.CREATE_USER_ID = USER.ID "
				+ "LEFT JOIN SYS_ORG AS ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE EXAM.STATE != 0 AND EXAM.CREATE_USER_ID = :CREATE_USER_ID";
		return getMapList(sql, new Object[] { userId });
	}

	@Override
	public Integer homeSubAdminPaper(Integer userId) {
		String sql = "SELECT COUNT(PAPER.ID) AS PAPER_NUM "
				+ "FROM EXM_PAPER PAPER "
				+ "WHERE PAPER.STATE != 0 AND PAPER.CREATE_USER_ID = :CREATE_USER_ID";
		return getCount(sql, new Object[] { userId });
	}

	@Override
	public Integer homeSubAdminQuestion(Integer userId) {
		String sql = "SELECT COUNT(QUESTION.ID) AS QUESTION_NUM "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE QUESTION.STATE != 0 AND QUESTION.CREATE_USER_ID = :CREATE_USER_ID";
		return getCount(sql, new Object[] { userId });
	}

	@Override
	public Integer homeSubAdminMark(Integer userId) {
		String sql = "SELECT COUNT(MARK.ID) "
				+ " FROM EXM_MY_MARK MARK INNER JOIN EXM_EXAM EXAM ON MARK.EXAM_ID = EXAM.ID "
				+ " WHERE EXAM.MARK_STATE != 3 AND MARK.UPDATE_USER_ID = :UPDATE_USER_ID";
		return getCount(sql, new Object[] { userId });
	}
	
	@Override
	public List<Map<String, Object>> homeAdminUser() {
		String sql = "SELECT SUM(USER.TYPE = 1)-1 AS USER_NUM, SUM(USER.TYPE = 2) AS SUBADMIN_NUM " // SUM(USER.TYPE = 1)-1 是减掉管理员
				+ "FROM SYS_USER USER WHERE USER.STATE != 0 ";
		return getMapList(sql, new Object[0]);
	}
	
	@Override
	public Integer homeAdminBulletin() {
		String sql = "SELECT COUNT(BULLETIN.ID) FROM EXM_BULLETIN BULLETIN WHERE BULLETIN.STATE != 0 AND BULLETIN.UPDATE_USER_ID = 1 ";
		return getCount(sql, new Object[0]);
	}
	
	@Override
	public List<Map<String, Object>> questionStatis(Integer questionTypeId) {
		String sql = "SELECT COUNT( * ) AS TOTAL, SUM(TYPE = 1) AS TYPE1, SUM(TYPE = 2) AS TYPE2, SUM(TYPE = 3) AS TYPE3, SUM(TYPE = 4) AS TYPE4, "
				+ "SUM(TYPE = 5) AS TYPE5, SUM(DIFFICULTY = 1) AS DIFFICULTY1, SUM(DIFFICULTY = 2) AS DIFFICULTY2, SUM(DIFFICULTY = 3) AS DIFFICULTY3, "
				+ "SUM(DIFFICULTY = 4) AS DIFFICULTY4, SUM(DIFFICULTY = 5) AS DIFFICULTY5, SUM(AI = 1) AS AI1, SUM(AI = 2) AS AI2 "
				+ "FROM EXM_QUESTION  WHERE STATE != 0 AND QUESTION_TYPE_ID = :QUESTION_TYPE_ID";
		return getMapList(sql, new Object[] { questionTypeId });
	}
	
	@Override
	public List<Map<String, Object>> examStatisType(Integer paperId) {
		String sql = "SELECT SUM( QUESTION.TYPE = 1 ) AS TYPE1, SUM( QUESTION.TYPE = 2 ) AS TYPE2, SUM( QUESTION.TYPE = 3 ) AS TYPE3, "
				+ "SUM( QUESTION.TYPE = 4 ) AS TYPE4, SUM( QUESTION.TYPE = 5 ) AS TYPE5 FROM EXM_QUESTION QUESTION "
				+ "WHERE QUESTION.STATE != 0 AND EXISTS ( SELECT 1 FROM EXM_PAPER_QUESTION PAPER_QUESTION WHERE PAPER_QUESTION.TYPE = 2 "
				+ "AND PAPER_QUESTION.PAPER_ID = :PAPER_ID AND PAPER_QUESTION.QUESTION_ID = QUESTION.ID) ";
		return getMapList(sql, new Object[] { paperId });
	}
	
	@Override
	public PageOut myExamListpage(PageIn pageIn) {
		String sql = "SELECT MY_EXAM.NO AS MY_EXAM_NO, USER.ID AS USER_ID, USER.NAME AS USER_NAME, USER.ORG_ID AS ORG_ID, MY_EXAM.ANSWER_STATE AS MY_EXAM_ANSWER_STATE, "
				+ "ORG.NAME AS ORG_NAME, MY_EXAM.STATE AS MY_EXAM_STATE, MY_EXAM.MARK_STATE AS MY_EXAM_MARK_STATE, "
				+ "MY_EXAM.TOTAL_SCORE AS MY_EXAM_SCORE, MY_EXAM.ANSWER_START_TIME AS MY_EXAM_START_TIME,  "
				+ "MY_EXAM.ANSWER_END_TIME AS MY_EXAM_END_TIME, MY_EXAM.MARK_START_TIME AS MY_EXAM_MARK_START_TIME,  "
				+ "MY_EXAM.MARK_END_TIME AS MY_EXAM_MARK_END_TIME, MY_EXAM.MARK_USER_ID AS MY_MARK_USER_ID, "
				+ "(SELECT MARK_USER.NAME FROM SYS_USER MARK_USER WHERE MARK_USER.ID = MY_EXAM.MARK_USER_ID ) AS MY_MARK_USER_NAME "
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN SYS_USER AS USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_ORG AS ORG ON USER.ORG_ID = ORG.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examId")), "MY_EXAM.EXAM_ID = :EXAM_ID", pageIn.get("examId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("questionId")) && ValidateUtil.isValid(pageIn.get("questionId")), "EXISTS ( SELECT 1 FROM EXM_MY_EXAM_DETAIL MY_EXAM_DETAIL "
						+ "WHERE MY_EXAM_DETAIL.EXAM_ID = MY_EXAM.EXAM_ID AND MY_EXAM_DETAIL.QUESTION_ID = :QUESTION_ID AND MY_EXAM_DETAIL.USER_ID = MY_EXAM.USER_ID "
						+ "AND MY_EXAM_DETAIL.QUESTION_SCORE != MY_EXAM_DETAIL.SCORE )", pageIn.get("questionId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("questionId")) && !ValidateUtil.isValid(pageIn.get("questionId")), "EXISTS ( SELECT 1 FROM EXM_MY_EXAM_DETAIL MY_EXAM_DETAIL "
						+ "WHERE MY_EXAM_DETAIL.EXAM_ID = MY_EXAM.EXAM_ID AND MY_EXAM_DETAIL.QUESTION_ID = :QUESTION_ID AND MY_EXAM_DETAIL.USER_ID = MY_EXAM.USER_ID "
						+ "AND MY_EXAM_DETAIL.QUESTION_SCORE = MY_EXAM_DETAIL.SCORE )", pageIn.get("questionId"))
				.addOrder("MY_EXAM.NO", Order.ASC)
				.addOrder("MY_EXAM.ANSWER_END_TIME", Order.DESC);
		return getListpage(sqlUtil, pageIn);
	}

	@Override
	public PageOut questionListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION.ID AS QUESTION_ID, QUESTION.TITLE AS QUESTION_TITLE, "
				+ "	MY_EXAM_DETAIL.USER_NUM, MY_EXAM_DETAIL.SUCC_USER_NUM "
				+ "FROM (SELECT MAX(QUESTION_ID) AS QUESTION_ID, COUNT(USER_ID) AS USER_NUM, SUM( SCORE = QUESTION_SCORE ) AS SUCC_USER_NUM "
				+ "			FROM EXM_MY_EXAM_DETAIL "
				+ "			WHERE EXAM_ID = :EXAM_ID"
				+ "			GROUP BY QUESTION_ID) MY_EXAM_DETAIL "
				+ "INNER JOIN EXM_QUESTION QUESTION ON MY_EXAM_DETAIL.QUESTION_ID = QUESTION.ID  ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil//.addWhere(ValidateUtil.isValid(pageIn.get("examId")), "", pageIn.get("examId"))
				.addFromParm(pageIn.get("examId"))
				.addOrder("MY_EXAM_DETAIL.SUCC_USER_NUM", Order.DESC);
		return getListpage(sqlUtil, pageIn);
	}
	
	@Override
	public List<Map<String, Object>> count(Integer examId) {
		String sql = "SELECT EXM_PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, EXM_PAPER.PASS_SCORE AS PAPER_PASS_SCORE, EXM_EXAM.START_TIME AS EXAM_START_TIME, "
				+ "EXM_EXAM.END_TIME AS EXAM_END_TIME, MAX( MY_EXAM.TOTAL_SCORE ) AS MAX, MIN( MY_EXAM.TOTAL_SCORE ) AS MIN, AVG( MY_EXAM.TOTAL_SCORE ) AS AVG, "
				+ "SUM( MY_EXAM.ANSWER_STATE = 1 ) AS EXAM_USER_ANSWER, COUNT( MY_EXAM.ID	) AS EXAM_USER_SUM, MAX( MY_EXAM.ANSWER_END_TIME ) AS MAX_EXAM, "
				+ "MIN( MY_EXAM.ANSWER_END_TIME ) AS MIN_EXAM FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM ON EXM_EXAM.ID = MY_EXAM.EXAM_ID "
				+ "INNER JOIN EXM_PAPER ON EXM_PAPER.ID = EXM_EXAM.PAPER_ID "
				+ "WHERE MY_EXAM.EXAM_ID = :EXAM_ID";
 		return getMapList(sql, new Object[] { examId });
	}
}