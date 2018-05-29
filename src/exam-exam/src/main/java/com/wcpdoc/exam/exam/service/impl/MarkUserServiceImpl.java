package com.wcpdoc.exam.exam.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.exam.dao.MarkUserDao;
import com.wcpdoc.exam.exam.entity.MarkUser;
import com.wcpdoc.exam.exam.service.MarkUserService;

/**
 * 判卷用户服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MarkUserServiceImpl extends BaseServiceImp<MarkUser> implements MarkUserService {
	@Resource
	private MarkUserDao markUserDao;

	@Override
	@Resource(name = "markUserDaoImpl")
	public void setDao(BaseDao<MarkUser> dao) {
		super.dao = dao;
	}

	@Override
	public void del(Integer examId, Integer userId) {
		markUserDao.del(examId, userId);
	}

	@Override
	public MarkUser getEntity(Integer examId, Integer userId) {
		return markUserDao.getEntity(examId, userId);
	}
}
