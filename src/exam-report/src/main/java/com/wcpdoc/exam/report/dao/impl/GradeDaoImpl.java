package com.wcpdoc.exam.report.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.report.dao.GradeDao;

/**
 * 统计数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class GradeDaoImpl extends RBaseDaoImpl<Object> implements GradeDao {
	@Resource
	private UserDao userDao;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<Map<String, Object>> homeUser(Integer userId) {
		String sql = "SELECT USER.ID AS USER_ID, USER.`NAME` AS USER_NAME, USER.ORG_ID AS ORG_ID ,ORG.`NAME` AS ORG_NAME, USER.TYPE AS TYPE, "
				+ "COUNT( MY_EXAM.TOTAL_SCORE ) AS EXAM_NUM, COUNT( MY_EXAM.STATE = 1 ) AS MISS_NUM, COUNT( MY_EXAM.ANSWER_STATE = 1 ) AS SUCC_NUM, "
				+ "IFNULL( MIN( MY_EXAM.NO ), 0 ) AS TOP, IFNULL( MAX( MY_EXAM.TOTAL_SCORE ), 0 ) AS MAX, IFNULL( MIN( MY_EXAM.TOTAL_SCORE ), 0 ) AS MIN, "
				+ "IFNULL( AVG( MY_EXAM.TOTAL_SCORE ), 0 ) AS AVG "
				+ "FROM EXM_MY_EXAM AS MY_EXAM "
				+ "INNER JOIN SYS_USER AS USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_ORG AS ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE MY_EXAM.STATE != 0 AND MY_EXAM.USER_ID = ? ";
 		return getMapList(sql, new Object[] { userId });
	}

	@Override
	public List<Map<String, Object>> homeSubAdminExam(Integer userId) {
		String sql = "SELECT USER.ID AS USER_ID, USER.`NAME` AS USER_NAME, USER.ORG_ID AS ORG_ID ,ORG.`NAME` AS ORG_NAME, "
				+ "USER.TYPE AS TYPE, COUNT(EXAM.ID) AS EXAM_NUM "
				+ "FROM EXM_EXAM AS EXAM "
				+ "INNER JOIN SYS_USER AS USER ON EXAM.CREATE_USER_ID = USER.ID "
				+ "LEFT JOIN SYS_ORG AS ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE EXAM.STATE != 0 AND EXAM.CREATE_USER_ID = ? ";
		return getMapList(sql, new Object[] { userId });
	}

	@Override
	public List<Map<String, Object>> homeSubAdminPaper(Integer userId) {
		String sql = "SELECT COUNT(PAPER.ID) AS PAPER_NUM "
				+ "FROM EXM_PAPER AS PAPER "
				+ "WHERE PAPER.STATE != 0 AND PAPER.CREATE_USER_ID = ? ";
		return getMapList(sql, new Object[] { userId });
	}

	@Override
	public List<Map<String, Object>> homeSubAdminQuestion(Integer userId) {
		String sql = "SELECT COUNT(QUESTION.ID) AS QUESTION_NUM "
				+ "FROM EXM_QUESTION AS QUESTION "
				+ "WHERE QUESTION.STATE != 0 AND QUESTION.CREATE_USER_ID = ? ";
		return getMapList(sql, new Object[] { userId });
	}

	@Override
	public List<Map<String, Object>> homeSubAdminMark(Integer userId) {
		String sql = "SELECT COUNT(MARK.ID) AS MARK_NUM "
				+ "FROM EXM_MY_MARK AS MARK "
				+ "WHERE MARK.UPDATE_USER_ID = ? ";
		return getMapList(sql, new Object[] { userId });
	}
	
	@Override
	public List<Map<String, Object>> homeAdminUser() {
		String sql = "SELECT SUM(USER.TYPE = 1)-1 AS USER_NUM, SUM(USER.TYPE = 2) AS SUBADMIN_NUM " // SUM(USER.TYPE = 1)-1 是减掉管理员
				+ "FROM SYS_USER AS USER WHERE USER.STATE != 0 ";
		return getMapList(sql, new Object[0]);
	}
	
	@Override
	public List<Map<String, Object>> homeAdminBulletin() {
		String sql = "SELECT COUNT(BULLETIN.ID) BULLETIN_NUM FROM EXM_BULLETIN AS BULLETIN WHERE BULLETIN.STATE != 0 AND BULLETIN.UPDATE_USER_ID = 1 ";
		return getMapList(sql, new Object[0]);
	}
	
	@Override
	public List<Map<String, Object>> count(Integer examId) {
		String sql = "SELECT EXM_PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, EXM_PAPER.PASS_SCORE AS PAPER_PASS_SCORE, EXM_EXAM.START_TIME AS EXAM_START_TIME, "
				+ "EXM_EXAM.END_TIME AS EXAM_END_TIME, MAX( MY_EXAM.TOTAL_SCORE ) AS MAX, MIN( MY_EXAM.TOTAL_SCORE ) AS MIN, AVG( MY_EXAM.TOTAL_SCORE ) AS AVG, "
				+ "SUM( MY_EXAM.ANSWER_STATE = 1 ) AS EXAM_USER_ANSWER, COUNT( MY_EXAM.ID	) AS EXAM_USER_SUM, MAX( MY_EXAM.ANSWER_END_TIME ) AS MAX_EXAM, "
				+ "MIN( MY_EXAM.ANSWER_END_TIME ) AS MIN_EXAM FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM ON EXM_EXAM.ID = MY_EXAM.EXAM_ID "
				+ "INNER JOIN EXM_PAPER ON EXM_PAPER.ID = EXM_EXAM.PAPER_ID "
				+ "WHERE MY_EXAM.EXAM_ID = ?";
 		return getMapList(sql, new Object[] { examId });
	}
}