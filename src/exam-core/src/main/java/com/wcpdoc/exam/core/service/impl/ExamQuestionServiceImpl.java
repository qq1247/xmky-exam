package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.ExamQuestionDao;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.service.ExamQuestionService;

/**
 * 考试试题服务层实现
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Service
public class ExamQuestionServiceImpl extends BaseServiceImp<ExamQuestion> implements ExamQuestionService {
	@Resource
	private ExamQuestionDao examQuestionDao;

	@Override
	public RBaseDao<ExamQuestion> getDao() {
		return examQuestionDao;
	}

	@Override
	public List<ExamQuestion> getList(Integer examId) {
		return examQuestionDao.getList(examId);
	}

	@Override
	public void clear(Integer examId) {
		examQuestionDao.clear(examId);
	}
}
