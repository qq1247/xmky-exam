package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.core.dao.QuestionAnswerDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionAnswer;

/**
 * 试题答案数据访问层实现
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
@Repository
public class QuestionAnswerDaoImpl extends RBaseDaoImpl<QuestionAnswer> implements QuestionAnswerDao {
	@Resource
	private UserDao userDao;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<QuestionAnswer> getList(Integer questionId) {
		String sql = "SELECT * FROM EXM_QUESTION_ANSWER WHERE QUESTION_ID = ?";
		return getList(sql, new Object[] { questionId }, QuestionAnswer.class);
	}

}