package com.wcpdoc.exam.report.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
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
		String sql = "SELECT USER.ID AS USER_ID, USER.NAME AS USER_NAME, USER.HEAD_FILE_ID AS USER_HEAD_FILE_ID, "
				+ "USER.ORG_ID AS ORG_ID, ORG.NAME AS ORG_NAME, USER.TYPE AS TYPE, "
				+ "EXAM.STATE AS EXAM_STATE, EXAM.START_TIME AS EXAM_START_TIME, EXAM.SCORE_STATE AS EXAM_SCORE_STATE, "
				+ "EXAM.RANK_STATE AS EXAM_RANK_STATE, MY_EXAM.STATE AS MY_EXAM_STATE, "
				+ "MY_EXAM.ANSWER_STATE AS MY_EXAM_ANSWER_STATE, MY_EXAM.NO AS MY_EXAM_NO, "
				+ "MY_EXAM.TOTAL_SCORE AS MY_EXAM_TOTAL_SCORE "
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM AS EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "INNER JOIN SYS_USER AS USER ON MY_EXAM.USER_ID = USER.ID "
				+ "LEFT JOIN SYS_ORG AS ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE MY_EXAM.STATE != 0 AND MY_EXAM.USER_ID = :USER_ID";
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
	public List<Map<String, Object>> examStatisType(Integer examId) {
		String sql = "SELECT SUM( QUESTION.TYPE = 1 ) AS TYPE1, SUM( QUESTION.TYPE = 2 ) AS TYPE2, SUM( QUESTION.TYPE = 3 ) AS TYPE3, "
				+ "SUM( QUESTION.TYPE = 4 ) AS TYPE4, SUM( QUESTION.TYPE = 5 ) AS TYPE5 FROM EXM_QUESTION QUESTION "
				+ "WHERE QUESTION.STATE != 0 AND EXISTS ( SELECT 1 FROM EXM_EXAM_QUESTION EXAM_QUESTION WHERE EXAM_QUESTION.TYPE = 2 "
				+ "AND EXAM_QUESTION.EXAM_ID = :EXAM_ID AND EXAM_QUESTION.QUESTION_ID = QUESTION.ID) ";
		return getMapList(sql, new Object[] { examId });
	}
	
	@Override
	public PageOut examRankListpage(PageIn pageIn) {
		String sql = "SELECT MY_EXAM.NO AS MY_EXAM_NO, " // 排名
				+ "USER.ID AS USER_ID, USER.NAME AS USER_NAME, ORG.NAME AS ORG_NAME, "// 用户机构信息
				+ "MY_EXAM.STATE AS MY_EXAM_STATE, MY_EXAM.MARK_STATE AS MY_EXAM_MARK_STATE, MY_EXAM.ANSWER_STATE AS MY_EXAM_ANSWER_STATE, "// 考试状态信息
				+ "MY_EXAM.ANSWER_START_TIME AS MY_EXAM_START_TIME, MY_EXAM.ANSWER_END_TIME AS MY_EXAM_END_TIME, "// 答题时间
				+ "MY_EXAM.MARK_START_TIME AS MY_EXAM_MARK_START_TIME, MY_EXAM.MARK_END_TIME AS MY_EXAM_MARK_END_TIME, "//阅卷时间  
				+ "MY_EXAM.TOTAL_SCORE AS MY_EXAM_TOTAL_SCORE "// 用户分数
				+ "FROM EXM_MY_EXAM MY_EXAM "// 我的考试
				+ "INNER JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID "// 考试用户
				+ "LEFT JOIN SYS_USER MARK_USER ON MY_EXAM.MARK_USER_ID = MARK_USER.ID "// 阅卷用户
				+ "LEFT JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID ";// 机构名称
		SqlUtil sqlUtil = new SqlUtil(sql)
				.addWhere("MY_EXAM.EXAM_ID = :EXAM_ID", pageIn.get("examId"))
				.addOrder("MY_EXAM.NO", Order.ASC)
				.addOrder("MY_EXAM.ANSWER_END_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"myExamStartTime", DateUtil.FORMAT_DATE_TIME, 
				"myExamEndTime", DateUtil.FORMAT_DATE_TIME, 
				"myExamMarkStartTime", DateUtil.FORMAT_DATE_TIME, 
				"myExamMarkEndTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> questionErrList(Integer examId) {
		String sql = "SELECT QUESTION.ID AS QUESTION_ID, QUESTION.TITLE AS QUESTION_TITLE, "
				+ "	MY_QUESTION.USER_NUM, MY_QUESTION.SUCC_USER_NUM "
				+ "FROM (SELECT QUESTION_ID, COUNT(USER_ID) AS USER_NUM, SUM( SCORE = USER_SCORE ) AS SUCC_USER_NUM "
				+ "			FROM EXM_MY_QUESTION "
				+ "			WHERE EXAM_ID = :EXAM_ID AND TYPE = 2 " 
				+ "			GROUP BY QUESTION_ID ) MY_QUESTION "
				+ "INNER JOIN EXM_QUESTION QUESTION ON MY_QUESTION.QUESTION_ID = QUESTION.ID "
				+ "ORDER BY MY_QUESTION.SUCC_USER_NUM DESC";
		return getMapList(sql, new Object[] { examId });
	}
	
	@Override
	public List<Map<String, Object>> count(Integer examId) {
		String sql = "SELECT EXM_EXAM.TOTAL_SCORE AS EXAM_TOTAL_SCORE, EXM_EXAM.PASS_SCORE AS EXAM_PASS_SCORE, EXM_EXAM.START_TIME AS EXAM_START_TIME, "
				+ "EXM_EXAM.END_TIME AS EXAM_END_TIME, MAX( MY_EXAM.TOTAL_SCORE ) AS MAX, MIN( MY_EXAM.TOTAL_SCORE ) AS MIN, AVG( MY_EXAM.TOTAL_SCORE ) AS AVG, "
				+ "SUM( MY_EXAM.ANSWER_STATE = 1 ) AS EXAM_USER_ANSWER, COUNT( MY_EXAM.ID ) AS EXAM_USER_SUM, MAX( MY_EXAM.ANSWER_END_TIME ) AS MAX_EXAM, "
				+ "MIN( MY_EXAM.ANSWER_END_TIME ) AS MIN_EXAM FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM ON EXM_EXAM.ID = MY_EXAM.EXAM_ID "
				+ "WHERE MY_EXAM.EXAM_ID = :EXAM_ID";
 		return getMapList(sql, new Object[] { examId });
	}

	@Override
	public Map<String, Object> adminHome() {
		String sql = "SELECT "
				+ "(SELECT COUNT(*) FROM EXM_EXAM WHERE (STATE = 1 OR STATE = 2)) AS EXAM_NUM, "// 考试数量
				+ "(SELECT COUNT(*) FROM EXM_QUESTION WHERE STATE = 1) AS QUESTION_NUM, "// 试题数量
				+ "(SELECT COUNT(*) FROM EXM_EXER WHERE STATE = 1) AS EXER_NUM, "// 练习数量
				+ "(SELECT COUNT(*) - 1 FROM SYS_USER WHERE STATE = 1 OR STATE = 2) AS USER_NUM "// 用户数量（不包括admin)
				+ "FROM DUAL";
		return getMapList(sql, new Object[0]).get(0);
	}

	@Override
	public Map<String, Object> userHome(Integer userId) {
		String sql = "SELECT "
				+ "COUNT(1) AS EXAM_NUM, "// 考试数量
//				+ "IFNULL(SUM(CASE WHEN (MY_EXAM.STATE = 1 AND MY_EXAM.MARK_STATE != 3) OR MY_EXAM.STATE = 2 THEN 1 ELSE 0 END), 0) AS UN_EXAM_NUM, "// 待考考试数量
				+ "(SELECT COUNT(*) FROM EXM_EXER WHERE STATE = 1 AND (USER_IDS LIKE :USER_ID2 OR USER_IDS IS NULL)) AS EXER_NUM, "// 练习数量
				+ "IFNULL(SUM(CASE WHEN EXAM.SCORE_STATE != 2 AND MY_EXAM.ANSWER_STATE = 1 THEN 1 ELSE 0 END), 0) AS PASS_EXAM_NUM, "// 及格次数
				+ "IFNULL(MIN(CASE WHEN EXAM.RANK_STATE = 1 THEN MY_EXAM.NO ELSE NULL END), 0) AS TOP_RANK "// 最高排名
				+ "FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM EXAM ON MY_EXAM.EXAM_ID = EXAM.ID "
				+ "WHERE MY_EXAM.USER_ID = :USER_ID AND EXAM.STATE = 1";// 查询当前用户，且有效的考试
		return getMapList(sql, new Object[]{ String.format("%%,%s,%%", userId), userId }).get(0);
	}
}