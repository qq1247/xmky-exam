package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.QuestionOptionDao;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 试题选项数据访问层实现
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
@Repository
public class QuestionOptionDaoImpl extends RBaseDaoImpl<QuestionOption> implements QuestionOptionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<QuestionOption> getList(Integer questionId) {
		String sql = "SELECT * FROM EXM_QUESTION_OPTION WHERE QUESTION_ID = :QUESTION_ID ORDER BY NO ASC";
		return getList(sql, new Object[] { questionId });
	}
}