package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyWrongQuestionDao;
import com.wcpdoc.exam.core.entity.MyWrongQuestion;
import com.wcpdoc.exam.core.service.MyWrongQuestionService;

import lombok.RequiredArgsConstructor;

/**
 * 我的错题服务层实现
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
@Service
@RequiredArgsConstructor
public class MyWrongQuestionServiceImpl extends BaseServiceImp<MyWrongQuestion> implements MyWrongQuestionService {
	private final MyWrongQuestionDao myWrongQuestionDao;

	@Override
	public RBaseDao<MyWrongQuestion> getDao() {
		return myWrongQuestionDao;
	}

	@Override
	public MyWrongQuestion getMyWrongQuestion(Integer userId, Integer questionId) {
		return myWrongQuestionDao.getMyWrongQuestion(userId, questionId);
	}

	@Override
	public List<MyWrongQuestion> getList(Integer userId) {
		return myWrongQuestionDao.getList(userId);
	}
}
