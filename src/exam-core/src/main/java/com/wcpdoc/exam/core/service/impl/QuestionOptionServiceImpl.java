package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.QuestionOptionDao;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.QuestionOptionService;

/**
 * 试题选项服务层实现
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
@Service
public class QuestionOptionServiceImpl extends BaseServiceImp<QuestionOption> implements QuestionOptionService {
	@Resource
	private QuestionOptionDao questionOptionDao;

	@Override
	public RBaseDao<QuestionOption> getDao() {
		return questionOptionDao;
	}
}
