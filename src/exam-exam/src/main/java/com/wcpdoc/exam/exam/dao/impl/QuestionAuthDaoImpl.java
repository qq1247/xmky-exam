package com.wcpdoc.exam.exam.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.dao.QuestionAuthDao;
import com.wcpdoc.exam.exam.entity.QuestionAuth;

/**
 * 试题授权数据访问层实现
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
@Repository
public class QuestionAuthDaoImpl extends BaseDaoImpl<QuestionAuth> implements QuestionAuthDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}
}