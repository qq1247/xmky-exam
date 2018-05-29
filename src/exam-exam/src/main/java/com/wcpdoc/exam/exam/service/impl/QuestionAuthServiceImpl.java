package com.wcpdoc.exam.exam.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.exam.entity.QuestionAuth;
import com.wcpdoc.exam.exam.service.QuestionAuthService;

/**
 * 试题授权服务层实现
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
@Service
public class QuestionAuthServiceImpl extends BaseServiceImp<QuestionAuth> implements QuestionAuthService {
	//private static final Logger log = LoggerFactory.getLogger(QuestionAuthServiceImpl.class);

	@Override
	@Resource(name = "questionAuthDaoImpl")
	public void setDao(BaseDao<QuestionAuth> dao) {
		super.dao = dao;
	}
}
