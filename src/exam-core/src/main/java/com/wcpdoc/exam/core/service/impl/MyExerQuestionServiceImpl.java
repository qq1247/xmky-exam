package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyExerQuestionDao;
import com.wcpdoc.exam.core.entity.MyExerQuestion;
import com.wcpdoc.exam.core.service.MyExerQuestionService;

import lombok.RequiredArgsConstructor;

/**
 * 我的练习试题服务层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Service
@RequiredArgsConstructor
public class MyExerQuestionServiceImpl extends BaseServiceImp<MyExerQuestion> implements MyExerQuestionService {
	private final MyExerQuestionDao myExerQuestionDao;

	@Override
	public RBaseDao<MyExerQuestion> getDao() {
		return myExerQuestionDao;
	}

	@Override
	public List<MyExerQuestion> getList(Integer myExerId) {
		return myExerQuestionDao.getList(myExerId);
	}

	@Override
	public MyExerQuestion getMyExerQuestion(Integer myExerId, Integer questionId) {
		return myExerQuestionDao.getMyExerQuestion(myExerId, questionId);
	}
}
