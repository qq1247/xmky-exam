package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
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
	public List<PaperQuestionAnswer> getList(Integer paperId, Integer questionId) {
		return paperQuestionAnswerDao.getList(paperId, questionId);
	}
	
	@Override
	public void updateAndDel(Integer id) {
		paperQuestionAnswerDao.del(id);
	}
}