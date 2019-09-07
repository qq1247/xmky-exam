package com.wcpdoc.exam.exam.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.ExamDao;
import com.wcpdoc.exam.exam.entity.Exam;
import com.wcpdoc.exam.exam.entity.ExamType;
import com.wcpdoc.exam.exam.entity.MarkUser;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 考试数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Repository
public class ExamDaoImpl extends BaseDaoImpl<Exam> implements ExamDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM.ID, EXAM.NAME, EXAM.PASS_SCORE, EXAM.START_TIME, EXAM.END_TIME, "
				+ "EXAM.STATE, PAPER.NAME AS PAPER_NAME, PAPER.TOTLE_SCORE AS PAPER_TOTLE_SCORE, "
				+ "EXAM.MARK_START_TIME, EXAM.MARK_END_TIME "
				+ "FROM EXM_EXAM EXAM "
				+ "LEFT JOIN EXM_EXAM_TYPE EXAM_TYPE ON EXAM.EXAM_TYPE_ID = EXAM_TYPE.ID "
				+ "LEFT JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		Date fiveDate = null;
		if(ValidateUtil.isValid(pageIn.getFive())){
			fiveDate = DateUtil.getDate(pageIn.getFive(), DateUtil.FORMAT_DATE_TIME);
		}
		Date sixDate = null;
		if(ValidateUtil.isValid(pageIn.getSix())){
			sixDate = DateUtil.getDate(pageIn.getSix(), DateUtil.FORMAT_DATE_TIME);
		}
		
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "EXAM.EXAM_TYPE_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "EXAM.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "PAPER.NAME LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getFour()), "EXAM.STATE = ?", pageIn.getFour())//0：删除；1：启用；2：禁用
				.addWhere(ValidateUtil.isValid(pageIn.getFive()), "EXAM.START_TIME > ?", fiveDate)
				.addWhere(ValidateUtil.isValid(pageIn.getSix()), "EXAM.START_TIME < ?", sixDate)
				.addWhere(ValidateUtil.isValid(pageIn.getEight()), 
						"(EXAM_TYPE.USER_IDS LIKE ? "
								+ "OR EXISTS (SELECT 1 FROM SYS_USER Z WHERE Z.ID = ? AND EXAM_TYPE.ORG_IDS LIKE CONCAT('%,', Z.ORG_ID, ',%')) "
								+ "OR EXISTS (SELECT 1 FROM SYS_POST_USER Z WHERE Z.USER_ID = ? AND EXAM_TYPE.POST_IDS LIKE CONCAT('%,', Z.POST_ID, ',%')))", 
						"%," + pageIn.getEight() + ",%", pageIn.getEight(), pageIn.getEight())
				.addWhere(ValidateUtil.isValid(pageIn.getNine()), "EXISTS (SELECT 1 FROM EXM_EXAM_USER Z WHERE Z.USER_ID = ? AND Z.EXAM_ID = EXAM.ID)", pageIn.getNine())
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXISTS (SELECT 1 FROM EXM_MARK_USER Z WHERE Z.USER_ID = ? AND Z.EXAM_ID = EXAM.ID)", pageIn.getTen())
				.addWhere("EXAM.STATE != ?", 0)
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getRows(), 
				"START_TIME", DateUtil.FORMAT_DATE_TIME, 
				"END_TIME", DateUtil.FORMAT_DATE_TIME, 
				"MARK_START_TIME", DateUtil.FORMAT_DATE_TIME, 
				"MARK_END_TIME", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "EXAM_STATE", "STATE");
		return pageOut;
	}

	@Override
	public PageOut getExamUserListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_USER.ID, USER.NAME AS USER_NAME, USER.LOGIN_NAME, ORG.NAME AS ORG_NAME, POST_USER.POST_NAMES "
				+ "FROM EXM_EXAM_USER EXAM_USER "
				+ "INNER JOIN SYS_USER USER ON EXAM_USER.USER_ID = USER.ID "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "LEFT JOIN (SELECT POST_USER.USER_ID, GROUP_CONCAT(POST.NAME) AS POST_NAMES "
				+ "				FROM SYS_POST_USER POST_USER "
				+ "				INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "				GROUP BY POST_USER.USER_ID ) POST_USER ON USER.ID = POST_USER.USER_ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORG.ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "USER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXAM_USER.EXAM_ID = ?", pageIn.getTen())
				.addWhere("USER.STATE = ?", 1)
				.addWhere("USER.ID != ?", 1)
				.addWhere("ORG.STATE = ?", 1)
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public PageOut getExamUserAddListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS USER_NAME, USER.LOGIN_NAME, ORG.NAME AS ORG_NAME, POST_USER.POST_NAMES "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "LEFT JOIN (SELECT POST_USER.USER_ID, GROUP_CONCAT(POST.NAME) AS POST_NAMES "
				+ "				FROM SYS_POST_USER POST_USER "
				+ "				INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "				GROUP BY POST_USER.USER_ID ) POST_USER ON USER.ID = POST_USER.USER_ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORG.ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "USER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "NOT EXISTS (SELECT 1 FROM EXM_EXAM_USER Z WHERE Z.EXAM_ID = ? AND USER.ID = Z.USER_ID)", pageIn.getTen())
				.addWhere("USER.STATE = ?", 1)
				.addWhere("USER.ID != ?", 1)
				.addWhere("ORG.STATE = ?", 1)
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public void doUserDel(Integer id, Integer userId) {
		String sql = "DELETE FROM EXM_EXAM_USER_QUESTION WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, id, userId);
	}

	@Override
	public ExamType getExamType(Integer id) {
		String sql = "SELECT EXAM_TYPE.* "
				+ "FROM EXM_EXAM_TYPE EXAM_TYPE "
				+ "INNER JOIN EXM_EXAM EXAM ON EXAM_TYPE.ID = EXAM.EXAM_TYPE_ID "
				+ "WHERE EXAM.ID = ?";
		return getUnique(sql, new Object[]{id}, ExamType.class);
	}

	@Override
	public List<Exam> getList(Integer examTypeId) {
		String sql = "SELECT * FROM EXM_EXAM EXAM_TYPE WHERE STATE = 1 AND EXAM_TYPE_ID = ?";
		return getList(sql, new Object[]{examTypeId}, Exam.class);
	}

	@Override
	public PageOut getMyExamListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_USER.ID, EXAM.NAME AS EXAM_NAME, PAPER.NAME AS PAPER_NAME, EXAM.START_TIME AS EXAM_START_TIME, "
				+ "		EXAM.END_TIME AS EXAM_END_TIME, PAPER.TOTLE_SCORE AS PAPER_TOTLE_SCORE, EXAM.PASS_SCORE AS EXAM_PASS_SCORE, "
				+ "		EXAM_USER.STATE AS EXAM_USER_STATE, EXAM_USER.TOTAL_SCORE AS EXAM_USER_TOTAL_SCORE "
				+ "FROM EXM_EXAM_USER EXAM_USER "
				+ "INNER JOIN EXM_EXAM EXAM ON EXAM_USER.EXAM_ID = EXAM.ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "INNER JOIN SYS_USER USER ON EXAM_USER.USER_ID = USER.ID";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXAM_USER.USER_ID =  ?", pageIn.getTen())
				.addWhere("EXAM.STATE = ?", 1)
//				.addWhere("PAPER.STATE = ?", 1)//删除了试卷也能查看
				.addWhere("USER.STATE = ?", 1)
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getRows(), "EXAM_START_TIME", DateUtil.FORMAT_DATE_TIME, "EXAM_END_TIME", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "EXAM_USER_STATE", "EXAM_USER_STATE");
		return pageOut;
	}

	@Override
	public PageOut getMarkUserListpage(PageIn pageIn) {
		String sql = "SELECT MARK_USER.ID, USER.NAME AS USER_NAME, USER.LOGIN_NAME, ORG.NAME AS ORG_NAME, POST_USER.POST_NAMES "
				+ "FROM EXM_MARK_USER MARK_USER "
				+ "INNER JOIN SYS_USER USER ON MARK_USER.USER_ID = USER.ID "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "LEFT JOIN (SELECT POST_USER.USER_ID, GROUP_CONCAT(POST.NAME) AS POST_NAMES "
				+ "				FROM SYS_POST_USER POST_USER "
				+ "				INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "				GROUP BY POST_USER.USER_ID ) POST_USER ON USER.ID = POST_USER.USER_ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORG.ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "USER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "MARK_USER.EXAM_ID = ?", pageIn.getTen())
				.addWhere("USER.STATE = ?", 1)
				.addWhere("USER.ID != ?", 1)
				.addWhere("ORG.STATE = ?", 1)
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public PageOut getMarkUserAddListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS USER_NAME, USER.LOGIN_NAME, ORG.NAME AS ORG_NAME, POST_USER.POST_NAMES "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "LEFT JOIN (SELECT POST_USER.USER_ID, GROUP_CONCAT(POST.NAME) AS POST_NAMES "
				+ "				FROM SYS_POST_USER POST_USER "
				+ "				INNER JOIN SYS_POST POST ON POST_USER.POST_ID = POST.ID "
				+ "				GROUP BY POST_USER.USER_ID ) POST_USER ON USER.ID = POST_USER.USER_ID";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "ORG.ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "USER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "NOT EXISTS (SELECT 1 FROM EXM_MARK_USER Z WHERE Z.EXAM_ID = ? AND USER.ID = Z.USER_ID)", pageIn.getTen())
				.addWhere("USER.STATE = ?", 1)
				.addWhere("USER.ID != ?", 1)
				.addWhere("ORG.STATE = ?", 1)
				.addOrder("USER.NAME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public PageOut getMarkListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_USER.ID, USER.NAME AS USER_NAME, ORG.NAME AS ORG_NAME, EXAM_USER.TOTAL_SCORE AS EXAM_USER_TOTAL_SCORE, "
				+ "MARK_USER.NAME AS MARK_USER_NAME, EXAM_USER.UPDATE_MARK_TIME AS EXAM_USER_UPDATE_MARK_TIME, PAPER.TOTLE_SCORE AS PAPER_TOTLE_SCORE "
				+ "FROM EXM_EXAM_USER EXAM_USER "
				+ "INNER JOIN EXM_EXAM EXAM ON EXAM_USER.EXAM_ID = EXAM.ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "INNER JOIN SYS_USER USER ON EXAM_USER.USER_ID = USER.ID "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "LEFT JOIN SYS_USER MARK_USER ON EXAM_USER.UPDATE_MARK_USER_ID = MARK_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()), "EXAM_USER.EXAM_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "USER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "ORG.NAME LIKE ? OR USER.NAME LIKE ?", "%" + pageIn.getThree() + "%", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXISTS (SELECT 1 FROM EXM_MARK_USER Z WHERE Z.USER_ID = ? AND Z.EXAM_ID = EXAM_USER.EXAM_ID)", pageIn.getTen())
				.addWhere("EXAM.STATE = ?", 1);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getRows(), 
				"EXAM_USER_UPDATE_MARK_TIME", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public void doForcePaper(LoginUser user) {
		String sql = "UPDATE EXM_EXAM_USER "
				+ "SET STATE = 4, UPDATE_USER_ID = ?, UPDATE_TIME = ? "
				+ "WHERE STATE <= 2 AND ID IN (SELECT ID FROM("
				+ "		SELECT Z.ID "
				+ "		FROM EXM_EXAM_USER Z "
				+ "		INNER JOIN EXM_EXAM Z1 ON Z.EXAM_ID = Z1.ID "
				+ "		WHERE Z1.END_TIME <= ?) A)";
		Date curTime = new Date();
		update(sql, new Object[]{user.getId(), curTime, curTime});
	}

	@Override
	public List<MarkUser> getMarkUserList(Integer id) {
		String sql = "SELECT * FROM EXM_MARK_USER WHERE EXAM_ID = ?";
		return getList(sql, new Object[]{id}, MarkUser.class);
	}

	@Override
	public PageOut getGradeListpage(PageIn pageIn) {
		String sql = "SELECT EXAM.NAME AS EXAM_NAME, EXAM.START_TIME AS EXAM_START_TIME, "
				+ "		EXAM.END_TIME AS EXAM_END_TIME, PAPER.NAME AS PAPER_NAME, USER.NAME AS USER_NAME, "
				+ "		EXAM_USER.TOTAL_SCORE, EXAM_USER.STATE AS EXAM_USER_STATE, MARK_USER.NAME AS MARK_USER_NAME, "
				+ "		EXAM_USER.UPDATE_MARK_TIME AS EXAM_USER_UPDATE_MARK_TIME "
				+ "FROM EXM_EXAM_USER EXAM_USER "
				+ "INNER JOIN EXM_EXAM EXAM ON EXAM.ID = EXAM_USER.EXAM_ID "
				+ "INNER JOIN EXM_PAPER PAPER ON EXAM.PAPER_ID = PAPER.ID "
				+ "INNER JOIN SYS_USER USER ON EXAM_USER.USER_ID = USER.ID "
				+ "INNER JOIN SYS_USER MARK_USER ON EXAM_USER.UPDATE_MARK_USER_ID = MARK_USER.ID ";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil//.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "EXAM.EXAM_TYPE_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "EXAM.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "PAPER.NAME LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getFour()), "USER.NAME LIKE ?", "%" + pageIn.getFour() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getFive()), "MARK.USER.NAME LIKE ?", "%" + pageIn.getFive() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getSix()), "EXAM_USER.STATE = ?", pageIn.getSix())
				.addOrder("EXAM.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getRows(), "EXAM_START_TIME", DateUtil.FORMAT_DATE_TIME, 
				"EXAM_END_TIME", DateUtil.FORMAT_DATE_TIME, "EXAM_USER_UPDATE_MARK_TIME", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "EXAM_USER_STATE", "EXAM_USER_STATE");
		return pageOut;
	}
}