package com.wcpdoc.exam.core.dao.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExerDao;
import com.wcpdoc.exam.core.entity.Exer;

/**
 * 练习数据访问层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Repository
public class ExerDaoImpl extends RBaseDaoImpl<Exer> implements ExerDao {
    @Resource
    private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXER.ID AS ID, EXER.NAME, EXER.START_TIME, EXER.END_TIME, "
				+ "EXER.RMK_STATE, EXER.USER_IDS, "
				+ "QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME "
				+ "FROM EXM_EXER EXER "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON EXER.QUESTION_TYPE_ID = QUESTION_TYPE.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeId", Integer.class)) , 
						"EXER.QUESTION_TYPE_ID = :QUESTION_TYPE_ID", pageIn.get("questionTypeId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("name")), "EXER.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), // 查询某个用户
						"(EXER.USER_IDS LIKE :USER_IDS OR EXER.USER_IDS IS NULL)", String.format("%%,%s,%%", pageIn.get("curUserId", Integer.class)))
				.addWhere("true".equals(pageIn.get("todo")), // 查找我的未完成的练习列表
						"EXER.START_TIME <= :START_TIME AND :END_TIME <= EXER.END_TIME", new Date(), new Date())
				.addWhere(ValidateUtil.isValid(pageIn.get("startTime")) && ValidateUtil.isValid(pageIn.get("endTime")), 
						"(( :START_TIME1 <= EXER.START_TIME AND EXER.START_TIME <= :END_TIME1) "
						+ "	OR ( :_START_TIME2 <= EXER.END_TIME AND EXER.END_TIME <= :END_TIME2) "
						+ "	OR ( :START_TIME3 >= EXER.START_TIME AND EXER.END_TIME >= :END_TIME3))", 
						pageIn.get("startTime"), pageIn.get("endTime"),
						pageIn.get("startTime"), pageIn.get("endTime"),
						pageIn.get("startTime"), pageIn.get("endTime")
						)
				.addWhere("EXER.STATE = 1")
				.addOrder("EXER.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "startTime", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDate(pageOut.getList(), "endTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<Exer> getList(Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_EXER WHERE QUESTION_TYPE_ID = :QUESTION_TYPE_ID AND STATE = 1 ";
		return getList(sql, new Object[] { questionTypeId });
	}
}