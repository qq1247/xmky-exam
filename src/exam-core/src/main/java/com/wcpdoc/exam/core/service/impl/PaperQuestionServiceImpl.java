package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
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
	public List<PaperQuestion> getQuestionList(Integer parentId) {
		return paperQuestionDao.getQuestionList(parentId);
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

}
