package com.wcpdoc.exam.core.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyExerQuestionDao;
import com.wcpdoc.exam.core.entity.MyExerQuestion;
import com.wcpdoc.exam.core.service.MyExerQuestionService;

/**
 * 我的练习试题服务层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Service
public class MyExerQuestionServiceImpl extends BaseServiceImp<MyExerQuestion> implements MyExerQuestionService {
	@Resource
	private MyExerQuestionDao myExerQuestionDao;

	@Override
	public RBaseDao<MyExerQuestion> getDao() {
		return myExerQuestionDao;
	}

	@Override
	public List<MyExerQuestion> getList(Integer exerId, Integer userId) {
		return myExerQuestionDao.getList(exerId, userId);
	}

	@Override
	public MyExerQuestion getMyExerQuestion(Integer exerId, Integer id, Integer questionId) {
		return myExerQuestionDao.getMyExerQuestion(exerId, id, questionId);
	}

	@Override
	public List<Map<String, Object>> getWrongAnswerNumList(Integer exerId) {
		return myExerQuestionDao.getWrongAnswerNumList(exerId);
	}
}
