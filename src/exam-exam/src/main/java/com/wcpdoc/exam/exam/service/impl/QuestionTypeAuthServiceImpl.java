package com.wcpdoc.exam.exam.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;
import com.wcpdoc.exam.exam.service.QuestionTypeAuthService;

/**
 * 试题分类权限服务层实现
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
@Service
@Deprecated
public class QuestionTypeAuthServiceImpl extends BaseServiceImp<QuestionTypeAuth> implements QuestionTypeAuthService {
//	@Resource
//	private QuestionTypeAuthDao questionTypeAuthDao;

	@Override
	@Resource(name = "questionTypeAuthDaoImpl")
	public void setDao(BaseDao<QuestionTypeAuth> dao) {
		super.dao = dao;
	}

//	@Override
//	public List<QuestionTypeAuth> getList() {
//		return questionTypeAuthDao.getList();
//	}
}
