package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
		String sql = "SELECT EXAM.ID, EXAM.NAME, EXAM.START_TIME, EXAM.END_TIME, EXAM.SCORE_STATE, EXAM.RANK_STATE, EXAM.LOGIN_TYPE, "
				+ "		EXAM.STATE, EXAM.PAPER_ID AS PAPER_ID, PAPER.NAME AS PAPER_NAME, PAPER.TOTAL_SCORE AS PAPER_TOTLE_SCORE, PAPER.PASS_SCORE AS PAPER_PASS_SCORE, " 
				+ "		EXAM.MARK_START_TIME, EXAM.MARK_END_TIME, USER.NAME AS USER_NAME, USER.ID AS USER_ID,"
				+ "		(SELECT COUNT(*) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = EXAM.ID) AS USER_NUM ," //考试人数
				+ "		(SELECT COUNT(*) FROM EXM_MY_MARK B WHERE B.EXAM_ID = EXAM.ID) AS MARK_NUM " //阅卷人数
				+ "FROM EXM_EXAM EXAM "
				+ "LEFT JOIN EXM_EXAM_TYPE EXAM_TYPE ON EXAM.EXAM_TYPE_ID = EXAM_TYPE.ID "
				+ "LEFT JOIN SYS_USER USER ON EXAM.UPDATE_USER_ID = USER.ID "
				+ "LEFT JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("examTypeId")), "EXAM.EXAM_TYPE_ID = ?", pageIn.get("examTypeId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("name")), "EXAM.NAME LIKE ?", "%" + pageIn.get("name") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("state")), "EXAM.STATE = ?", pageIn.get("state"))//0：删除；1：启用；2：禁用
				.addWhere(ValidateUtil.isValid(pageIn.get("startTime")), "EXAM.START_TIME > ?", ValidateUtil.isValid(pageIn.get("START_TIME")) ? DateUtil.getDateTime(pageIn.get("START_TIME")) : null)
				.addWhere(ValidateUtil.isValid(pageIn.get("startTime")), "EXAM.START_TIME < ?", ValidateUtil.isValid(pageIn.get("START_TIME")) ? DateUtil.getDateTime(pageIn.get("START_TIME")) : null)
				//.addWhere(pageIn.get("curUserId", Integer.class) != null, "EXISTS (SELECT 1 FROM EXM_MY_MARK Z WHERE Z.MARK_USER_ID = ? AND Z.EXAM_ID = EXAM.ID)", pageIn.get("curUserId", Integer.class))
				.addWhere("EXAM.STATE != ?", 0)
				.addOrder("EXAM.START_TIME", Order.DESC);
		
		if (pageIn.get("curUserId", Integer.class) != null) {//查看权限相关
			User user = userDao.getEntity(pageIn.get("curUserId", Integer.class));
			StringBuilder partSql = new StringBuilder();
			List<Object> params = new ArrayList<>();
			partSql.append("(");
			partSql.append("EXAM_TYPE.READ_USER_IDS LIKE ? ");
			params.add("%" + user.getId() + "%");
			
			partSql.append("OR EXAM_TYPE.WRITE_USER_IDS LIKE ? ");
			params.add("%" + user.getOrgId() + "%");
			
			/*if (ValidateUtil.isValid(user.getPostIds())) {
				String[] postIds = user.getPostIds().substring(1, user.getPostIds().length() - 1).split(",");
				for (String postId : postIds) {
					partSql.append("OR EXAM_TYPE.POST_IDS LIKE ? ");
					params.add("%" + postId + "%");
				}
			}*/
			partSql.append(")");
			
			sqlUtil.addWhere(partSql.toString(), params.toArray(new Object[params.size()]));
		}
		
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
		String sql = "SELECT * FROM EXM_EXAM EXAM_TYPE WHERE STATE = 1 AND EXAM_TYPE_ID = ?";
		return getList(sql, new Object[]{examTypeId}, Exam.class);
	}

	@Override
	public void doForcePaper(LoginUser user) {
		String sql = "UPDATE EXM_MY_EXAM "
				+ "SET STATE = 4, UPDATE_USER_ID = ?, UPDATE_TIME = ? "
				+ "WHERE STATE <= 2 AND ID IN (SELECT ID FROM("
				+ "		SELECT Z.ID "
				+ "		FROM EXM_MY_EXAM Z "
				+ "		INNER JOIN EXM_EXAM Z1 ON Z.EXAM_ID = Z1.ID "
				+ "		WHERE Z1.END_TIME <= ?) A)";
		Date curTime = new Date();
		update(sql, new Object[]{user.getId(), curTime, curTime});
	}

	@Override
	public PageOut getGradeListpage(PageIn pageIn) {
		return null;
//		String sql = "SELECT EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, "
//				+ "		EXAM.END_TIME AS EXAM_END_TIME, PAPER.NAME AS PAPER_NAME, USER.NAME AS USER_NAME, "
//				+ "		MY_EXAM.TOTAL_SCORE, MY_EXAM.STATE AS MY_EXAM_STATE, MY_MARK.NAME AS MY_MARK_NAME, "
//				+ "		MY_EXAM.UPDATE_MARK_TIME AS MY_EXAM_UPDATE_MARK_TIME "
//				+ "FROM EXM_MY_EXAM MY_EXAM "
//				+ "INNER JOIN EXM_EXAM EXAM ON EXAM.ID = MY_EXAM.EXAM_ID "
//				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
//				+ "INNER JOIN SYS_USER USER ON MY_EXAM.USER_ID = USER.ID "
//				+ "INNER JOIN SYS_USER MY_MARK ON MY_EXAM.UPDATE_MARK_USER_ID = MY_MARK.ID ";
//		
//		SqlUtil sqlUtil = new SqlUtil(sql);
//		sqlUtil//.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "EXAM.EXAM_TYPE_ID = ?", pageIn.getOne())
//				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "EXAM.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
//				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "PAPER.NAME LIKE ?", "%" + pageIn.getThree() + "%")
//				.addWhere(ValidateUtil.isValid(pageIn.getFour()), "USER.NAME LIKE ?", "%" + pageIn.getFour() + "%")
//				.addWhere(ValidateUtil.isValid(pageIn.getFive()), "MARK.USER.NAME LIKE ?", "%" + pageIn.getFive() + "%")
//				.addWhere(ValidateUtil.isValid(pageIn.getSix()), "MY_EXAM.STATE = ?", pageIn.getSix())
//				.addOrder("EXAM.START_TIME", Order.DESC);
//		PageOut pageOut = getListpage(sqlUtil, pageIn);
//		HibernateUtil.formatDate(pageOut.getRows(), "EXAM_START_TIME", DateUtil.FORMAT_DATE_TIME, 
//				"EXAM_END_TIME", DateUtil.FORMAT_DATE_TIME, "MY_EXAM_UPDATE_MARK_TIME", DateUtil.FORMAT_DATE_TIME);
//		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "MY_EXAM_STATE", "MY_EXAM_STATE");
//		return pageOut;
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
				+ "WHERE USER.STATE = 1 AND EXISTS (SELECT 1 FROM EXM_MY_EXAM Z WHERE Z.EXAM_ID = ? AND USER.ID = Z.USER_ID) "
				+ "ORDER BY USER.UPDATE_TIME DESC ";
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