package com.wcpdoc.exam.exam.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.exam.dao.ExamUserDao;
import com.wcpdoc.exam.exam.entity.ExamUser;
import com.wcpdoc.exam.exam.service.ExamUserService;

/**
 * 考试用户服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class ExamUserServiceImpl extends BaseServiceImp<ExamUser> implements ExamUserService {
	@Resource
	private ExamUserDao examUserDao;

	@Override
	@Resource(name = "examUserDaoImpl")
	public void setDao(BaseDao<ExamUser> dao) {
		super.dao = dao;
	}

	@Override
	public void delete(Integer examId, Integer userId) {
		examUserDao.delete(examId, userId);
	}

	@Override
	public ExamUser getEntity(Integer examId, Integer userId) {
		return examUserDao.getEntity(examId, userId);
	}
}
