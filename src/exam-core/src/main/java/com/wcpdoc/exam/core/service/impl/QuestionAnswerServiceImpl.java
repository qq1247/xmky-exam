package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.QuestionAnswerDao;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.service.QuestionAnswerService;

/**
 * 试题服务层实现
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
@Service
public class QuestionAnswerServiceImpl extends BaseServiceImp<QuestionAnswer> implements QuestionAnswerService {
	@Resource
	private QuestionAnswerDao questionAnswerDao;

	@Override
	public RBaseDao<QuestionAnswer> getDao() {
		return questionAnswerDao;
	}
}