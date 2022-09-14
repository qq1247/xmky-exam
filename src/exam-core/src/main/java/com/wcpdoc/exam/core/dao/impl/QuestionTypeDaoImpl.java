package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 题库数据访问层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Repository
public class QuestionTypeDaoImpl extends RBaseDaoImpl<QuestionType> implements QuestionTypeDao {
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION_TYPE.ID, QUESTION_TYPE.NAME, "
				+ "(SELECT COUNT(*) FROM EXM_QUESTION Z WHERE Z.QUESTION_TYPE_ID = QUESTION_TYPE.ID) AS QUESTION_NUM "
				+ "FROM EXM_QUESTION_TYPE QUESTION_TYPE ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "QUESTION_TYPE.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "QUESTION_TYPE.UPDATE_USER_ID = :UPDATE_USER_IDS", pageIn.get("curUserId", Integer.class))
				.addOrder("QUESTION_TYPE.UPDATE_TIME", Order.DESC);
		return getListpage(sqlUtil, pageIn);
	}
}