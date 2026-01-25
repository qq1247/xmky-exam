package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyCourseQuestionDao;
import com.wcpdoc.exam.core.entity.MyCourseQuestion;
import com.wcpdoc.exam.core.service.MyCourseQuestionService;

import lombok.RequiredArgsConstructor;

/**
 * 我的课程资料服务层实现
 * 
 * v1.0 zhanghc 2026-01-23 09:50:50
 */
@Service
@RequiredArgsConstructor
public class MyCourseQuestionServiceImpl extends BaseServiceImp<MyCourseQuestion> implements MyCourseQuestionService {
	private final MyCourseQuestionDao myCourseQuestionDao;

	@Override
	public RBaseDao<MyCourseQuestion> getDao() {
		return myCourseQuestionDao;
	}

	@Override
	public List<MyCourseQuestion> getList(Integer userId, Integer courseMaterialId) {
		return myCourseQuestionDao.getList(userId, courseMaterialId);
	}

	@Override
	public MyCourseQuestion getMyCourseQuestion(Integer userId, Integer courseMaterialId, Integer questionId) {
		return myCourseQuestionDao.getMyCourseQuestion(userId, courseMaterialId, questionId);
	}
}
