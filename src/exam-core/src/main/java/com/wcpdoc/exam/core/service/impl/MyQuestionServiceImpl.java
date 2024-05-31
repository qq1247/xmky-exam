package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.MyQuestionDao;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyQuestionService;

/**
 * 我的试题服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyQuestionServiceImpl extends BaseServiceImp<MyQuestion> implements MyQuestionService {
	@Resource
	private MyQuestionDao myQuestionDao;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	@Lazy
	private MyExamService myExamService;

	@Override
	public RBaseDao<MyQuestion> getDao() {
		return myQuestionDao;
	}

	@Override
	public MyQuestion getMyQuestion(Integer examId, Integer userId, Integer questionId) {
		return myQuestionDao.getMyQuestion(examId, userId, questionId);
	}

	@Override
	public List<MyQuestion> getList(Integer examId, Integer userId) {
		return myQuestionDao.getList(examId, userId);
	}

	@Override
	@CacheEvict(value = ExamConstant.MYQUESTION_CACHE, allEntries = true)
	public void clear(Integer examId) {
		myQuestionDao.clear(examId);
	}

	@Override
	public List<Integer> getExamIdList(Integer questionId) {
		return myQuestionDao.getExamIdList(questionId);
	}
}
