package com.wcpdoc.exam.core.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.PaperQuestionDao;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.PaperQuestionService;

/**
 * 试卷试题服务层实现
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Service
public class PaperQuestionServiceImpl extends BaseServiceImp<PaperQuestion> implements PaperQuestionService {
	@Resource
	private PaperQuestionDao paperQuestionDao;

	@Override
	@Resource(name = "paperQuestionDaoImpl")
	public void setDao(BaseDao<PaperQuestion> dao) {
		super.dao = dao;
	}

	@Override
	public List<PaperQuestion> getQuestionList(Integer parentId, Integer examId, Integer userId) {
		return paperQuestionDao.getQuestionList(parentId, examId, userId);
	}

	@Override
	public List<PaperQuestion> getList(Integer paperId) {
		return paperQuestionDao.getList(paperId);
	}

	@Override
	public List<PaperQuestion> getChapterList(Integer paperId) {
		return paperQuestionDao.getChapterList(paperId);
	}

	@Override
	public PaperQuestion getEntity(Integer paperId, Integer questionId) {
		return paperQuestionDao.getEntity(paperId, questionId);
	}

	@Override
	public void del(Integer examId, Integer userId) {
		paperQuestionDao.del(examId, userId);
	}

	@Override
	public List<Question> getQuestionRandList(Integer examId, Integer paperId) {
		return paperQuestionDao.getQuestionRandList(examId, paperId);
	}
	
	@Override
	public List<Map<String, Object>> questionAnswerList(Integer examId, Integer paperId, Integer questionId) {
		return paperQuestionDao.questionAnswerList(examId, paperId, questionId);
	}
	
	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer examId, Integer paperId) {
		return paperQuestionDao.getPaperQuestionList(examId, paperId);
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer questionId) {
		return paperQuestionDao.getPaperQuestionList(questionId);
	}
}
