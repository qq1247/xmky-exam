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
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 试题分类数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Repository
public class QuestionTypeDaoImpl extends RBaseDaoImpl<QuestionType> implements QuestionTypeDao {
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION_TYPE.ID, QUESTION_TYPE.NAME, QUESTION_TYPE.WRITE_USER_IDS, "
				+ "QUESTION_TYPE.CREATE_USER_ID, USER.NAME AS CREATE_USER_NAME "
				+ "FROM EXM_QUESTION_TYPE QUESTION_TYPE "
				+ "INNER JOIN SYS_USER USER ON QUESTION_TYPE.CREATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "QUESTION_TYPE.NAME LIKE :QUESTION_TYPE.NAME", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "QUESTION_TYPE.WRITE_USER_IDS LIKE :QUESTION_TYPE.WRITE_USER_IDS", String.format("%%%s%%", pageIn.get("curUserId", Integer.class)))
				.addWhere("QUESTION_TYPE.STATE = 1")
				.addOrder("QUESTION_TYPE.UPDATE_TIME", Order.DESC);
		return getListpage(sqlUtil, pageIn);
	}
}