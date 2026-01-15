package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.CourseQuestionDao;
import com.wcpdoc.exam.core.entity.CourseQuestion;
import com.wcpdoc.exam.core.service.CourseQuestionService;

import lombok.RequiredArgsConstructor;

/**
 * 课程试题服务层实现
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
@Service
@RequiredArgsConstructor
public class CourseQuestionServiceImpl extends BaseServiceImp<CourseQuestion> implements CourseQuestionService {
	private final CourseQuestionDao courseQuestionDao;

	@Override
	public RBaseDao<CourseQuestion> getDao() {
		return courseQuestionDao;
	}

	@Override
	public List<CourseQuestion> getList(Integer courseMaterialId) {
		return courseQuestionDao.getList(courseMaterialId);
	}
}
