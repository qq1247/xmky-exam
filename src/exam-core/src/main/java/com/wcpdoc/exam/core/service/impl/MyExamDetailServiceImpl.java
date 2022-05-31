package com.wcpdoc.exam.core.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyExamDetailDao;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.service.MyExamDetailService;

/**
 * 我的考试详细服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyExamDetailServiceImpl extends BaseServiceImp<MyExamDetail> implements MyExamDetailService {
	@Resource
	private MyExamDetailDao myExamDetailDao;
	
	@Override
	@Resource(name = "myExamDetailDaoImpl")
	public void setDao(BaseDao<MyExamDetail> dao) {
		super.dao = dao;
	}

	@Override
	public MyExamDetail getMyExamDetail(Integer examId, Integer userId, Integer questionId) {
		return myExamDetailDao.getMyExamDetail(examId, userId, questionId);
	}

	@Override
	public List<MyExamDetail> getList(Integer examId, Integer userId) {
		return myExamDetailDao.getList(examId, userId);
	}

	@Override
	public List<Map<String, Object>> getAnswerList(Integer examId, Integer userId) {
		return myExamDetailDao.getAnswerList(examId, userId);
	}
	
}
