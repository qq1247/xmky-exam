package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.PaperQuestionDao;
import com.wcpdoc.exam.core.entity.PaperQuestion;
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
	public List<PaperQuestion> getChapterList(Integer paperId) {
		return paperQuestionDao.getChapterList(paperId);
	}

	@Override
	public List<PaperQuestion> getChapterDetailList(Integer chapterId) {
		return paperQuestionDao.getChapterDetailList(chapterId);
	}

	@Override
	public PaperQuestion getEntity(Integer paperId, Integer questionId) {
		return paperQuestionDao.getEntity(paperId, questionId);
	}

	@Override
	public List<PaperQuestion> getList(Integer examId, Integer userId) {
		return paperQuestionDao.getList(examId, userId);
	}

	@Override
	public PaperQuestion getEntity(Integer id, Integer userId, Integer questionId) {
		return paperQuestionDao.getEntity(id, userId, questionId);
	}

	@Override
	public List<PaperQuestion> getList2(Integer questionId) {
		return paperQuestionDao.getList(questionId);
	}
}
