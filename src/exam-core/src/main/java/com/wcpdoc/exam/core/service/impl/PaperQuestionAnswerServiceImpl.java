package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.PaperQuestionAnswerDao;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.service.PaperQuestionAnswerService;

/**
 * 试题服务层实现
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
@Service
public class PaperQuestionAnswerServiceImpl extends BaseServiceImp<PaperQuestionAnswer> implements PaperQuestionAnswerService {
	@Resource
	private PaperQuestionAnswerDao paperQuestionAnswerDao;

	@Override
	@Resource(name = "paperQuestionAnswerDaoImpl")
	public void setDao(BaseDao<PaperQuestionAnswer> dao) {
		super.dao = dao;
	}
	

	@Override
	public List<PaperQuestionAnswer> getList(Integer paperId) {
		return paperQuestionAnswerDao.getList(paperId);
	}
	
	@Override
	public List<PaperQuestionAnswer> getList(Integer examId, Integer userId) {
		return paperQuestionAnswerDao.getList(examId, userId);
	}

	@Override
	public List<PaperQuestionAnswer> getListBySingleQuestion(Integer paperId, Integer questionId) {
		return paperQuestionAnswerDao.getListForSingleQuestion(paperId, questionId);
	}


	@Override
	public List<PaperQuestionAnswer> getListByChapter(Integer chapterId) {
		return paperQuestionAnswerDao.getListByChapter(chapterId);
	}
}