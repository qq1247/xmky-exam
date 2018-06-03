package com.wcpdoc.exam.exam.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.dao.QuestionTypeAuthDao;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;

/**
 * 试题分类权限数据访问层实现
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
@Repository
public class QuestionTypeAuthDaoImpl extends BaseDaoImpl<QuestionTypeAuth> implements QuestionTypeAuthDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<QuestionTypeAuth> getList() {
		String sql = "SELECT * FROM EXM_QUESTION_TYPE_AUTH";
		return getList(sql, QuestionTypeAuth.class);
	}
}