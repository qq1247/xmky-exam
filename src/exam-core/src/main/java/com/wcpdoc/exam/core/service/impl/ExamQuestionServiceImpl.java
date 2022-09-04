package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
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
	@Resource(name = "examQuestionDaoImpl")
	public void setDao(BaseDao<ExamQuestion> dao) {
		super.dao = dao;
	}

	@Override
	public List<ExamQuestion> getChapterList(Integer examId) {
		return examQuestionDao.getChapterList(examId);
	}

	@Override
	public List<ExamQuestion> getChapterDetailList(Integer examId, Integer no) {
		return examQuestionDao.getChapterDetailList(examId, no);
	}

	@Override
	public ExamQuestion getEntity(Integer examId, Integer questionId) {
		return examQuestionDao.getEntity(examId, questionId);
	}

	@Override
	public List<ExamQuestion> getList(Integer examId, Integer userId) {
		return examQuestionDao.getList(examId, userId);
	}

	@Override
	public ExamQuestion getEntity(Integer id, Integer userId, Integer questionId) {
		return examQuestionDao.getEntity(id, userId, questionId);
	}

	@Override
	public List<ExamQuestion> getList(Integer questionId) {
		return examQuestionDao.getList(questionId);
	}
}
