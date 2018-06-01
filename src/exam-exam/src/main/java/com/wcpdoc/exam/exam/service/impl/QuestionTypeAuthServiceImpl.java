package com.wcpdoc.exam.exam.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.exam.dao.QuestionTypeAuthDao;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;
import com.wcpdoc.exam.exam.service.QuestionTypeAuthService;

/**
 * 试题分类权限服务层实现
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
@Service
public class QuestionTypeAuthServiceImpl extends BaseServiceImp<QuestionTypeAuth> implements QuestionTypeAuthService {
	@Resource
	private QuestionTypeAuthDao questionTypeAuthDao;

	@Override
	@Resource(name = "questionTypeAuthDaoImpl")
	public void setDao(BaseDao<QuestionTypeAuth> dao) {
		super.dao = dao;
	}

	@Override
	public void delByQuestionTypeId(Integer questionTypeId) {
		questionTypeAuthDao.delByQuestionTypeId(questionTypeId);
	}

	@Override
	public QuestionTypeAuth getEntityByQuestionTypeId(Integer questionTypeId) {
		return questionTypeAuthDao.getEntityByQuestionTypeId(questionTypeId);
	}
}
