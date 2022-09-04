package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.ExamQuestionNoDao;
import com.wcpdoc.exam.core.entity.ExamQuestionNo;
import com.wcpdoc.exam.core.service.ExamQuestionNoService;

/**
 * 考试试题排序服务层实现
 * 
 * v1.0 chenyun 2017-05-26 14:23:38
 */
@Service
public class ExamQuestionNoServiceImpl extends BaseServiceImp<ExamQuestionNo> implements ExamQuestionNoService {
	@Resource
	private ExamQuestionNoDao examQuestionNoDao;

	@Override
	@Resource(name = "examQuestionNoDaoImpl")
	public void setDao(BaseDao<ExamQuestionNo> dao) {
		super.dao = dao;
	}

	@Override
	public ExamQuestionNo getEntity(Integer examId, Integer userId) {
		return examQuestionNoDao.getEntity(examId, userId);
	}
}
