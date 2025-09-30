package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyFavQuestionDao;
import com.wcpdoc.exam.core.entity.MyFavQuestion;
import com.wcpdoc.exam.core.service.MyFavQuestionService;

/**
 * 我的收藏试题服务层实现
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
@Service
public class MyFavQuestionServiceImpl extends BaseServiceImp<MyFavQuestion> implements MyFavQuestionService {
	@Resource
	private MyFavQuestionDao myFavQuestionDao;

	@Override
	public RBaseDao<MyFavQuestion> getDao() {
		return myFavQuestionDao;
	}

	@Override
	public MyFavQuestion getMyFavQuestion(Integer userId, Integer questionId) {
		return myFavQuestionDao.getMyFavQuestion(userId, questionId);
	}

	@Override
	public List<MyFavQuestion> getList(Integer userId) {
		return myFavQuestionDao.getList(userId);
	}
}
