package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;

/**
 * 我的阅卷服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyMarkServiceImpl extends BaseServiceImp<MyMark> implements MyMarkService {
	@Resource
	private MyMarkDao myMarkDao;

	@Override
	@Resource(name = "myMarkDaoImpl")
	public void setDao(BaseDao<MyMark> dao) {
		super.dao = dao;
	}

	@Override
	public void del(Integer examId, Integer userId) {
		myMarkDao.del(examId, userId);
	}

	@Override
	public MyMark getEntity(Integer examId, Integer userId) {
		return myMarkDao.getEntity(examId, userId);
	}

	@Override
	public List<MyMark> getList(Integer examId) {
		return myMarkDao.getList(examId);
	}
}
