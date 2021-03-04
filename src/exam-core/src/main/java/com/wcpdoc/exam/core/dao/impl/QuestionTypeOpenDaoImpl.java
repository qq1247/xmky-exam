package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.QuestionTypeOpenDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试题分类数据访问层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Repository
public class QuestionTypeOpenDaoImpl extends RBaseDaoImpl<QuestionTypeOpen> implements QuestionTypeOpenDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT * "
				+ "FROM EXM_QUESTION_TYPE_OPEN QUESTION_TYPE_OPEN ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "QUESTION_TYPE_OPEN.ID = ?", pageIn.getTwo())
			   .addWhere(ValidateUtil.isValid(pageIn.getThree()), "QUESTION_TYPE_OPEN.STATE = ?", pageIn.getThree())
				;
		PageOut pageOut = getListpage(sqlUtil, pageIn);
				HibernateUtil.formatDate(pageOut.getRows(), "START_TIME", DateUtil.FORMAT_DATE_TIME);
				HibernateUtil.formatDate(pageOut.getRows(), "END_TIME", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}
}