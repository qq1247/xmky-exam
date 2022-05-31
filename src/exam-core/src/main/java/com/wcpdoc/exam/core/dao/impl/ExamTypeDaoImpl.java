package com.wcpdoc.exam.core.dao.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExamTypeDao;
import com.wcpdoc.exam.core.entity.ExamType;

/**
 * 考试分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Repository
public class ExamTypeDaoImpl extends RBaseDaoImpl<ExamType> implements ExamTypeDao {
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM_TYPE.ID, EXAM_TYPE.NAME "
				+ "FROM EXM_EXAM_TYPE EXAM_TYPE "
				+ "INNER JOIN SYS_USER USER ON EXAM_TYPE.CREATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "EXAM_TYPE.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "EXAM_TYPE.CREATE_USER_ID = :CREATE_USER_ID", pageIn.get("curUserId", Integer.class))
				.addWhere("EXAM_TYPE.STATE = 1")
				.addOrder("EXAM_TYPE.UPDATE_TIME", Order.DESC);
		return getListpage(sqlUtil, pageIn);
	}
}