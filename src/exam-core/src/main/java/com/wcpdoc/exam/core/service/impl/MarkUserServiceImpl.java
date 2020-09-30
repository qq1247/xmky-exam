package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.MarkUserDao;
import com.wcpdoc.exam.core.entity.MarkUser;
import com.wcpdoc.exam.core.service.MarkUserService;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;

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

	@Override
	public List<MarkUser> getList(Integer examId) {
		return markUserDao.getList(examId);
	}
}
