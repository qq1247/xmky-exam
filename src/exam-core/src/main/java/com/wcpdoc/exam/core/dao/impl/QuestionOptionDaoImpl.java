package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.QuestionOptionDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试题选项数据访问层实现
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
@Repository
public class QuestionOptionDaoImpl extends RBaseDaoImpl<QuestionOption> implements QuestionOptionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT * "
				+ "FROM EXM_QUESTION_OPTION QUESTION_OPTION ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "QUESTION_OPTION.ID = ?", pageIn.getTwo());
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public QuestionOption getQuestionOption(Integer questionId) {
		String sql = "SELECT * FROM EXM_QUESTION_OPTION WHERE QUESTION_ID = ? ";
		return getEntity(sql, new Object[]{questionId});
	}
}