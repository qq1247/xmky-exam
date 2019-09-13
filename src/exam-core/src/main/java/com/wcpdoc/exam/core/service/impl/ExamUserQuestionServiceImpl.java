package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.ExamUserQuestionDao;
import com.wcpdoc.exam.core.entity.ExamUserQuestion;
import com.wcpdoc.exam.core.service.ExamUserQuestionService;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;

/**
 * 考试用户试题服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class ExamUserQuestionServiceImpl extends BaseServiceImp<ExamUserQuestion> implements ExamUserQuestionService {
	@Resource
	private ExamUserQuestionDao examUserQuestionDao;

	@Override
	@Resource(name = "examUserQuestionDaoImpl")
	public void setDao(BaseDao<ExamUserQuestion> dao) {
		super.dao = dao;
	}

	@Override
	public List<ExamUserQuestion> getList(Integer examUserId) {
		return examUserQuestionDao.getList(examUserId);
	}

	@Override
	public List<ExamUserQuestion> getList(Integer examId, Integer userId) {
		return examUserQuestionDao.getList(examId, userId);
	}
}
