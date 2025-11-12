package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.ExamRuleDao;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.service.ExamRuleService;

import lombok.RequiredArgsConstructor;

/**
 * 考试规则服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
@RequiredArgsConstructor
public class ExamRuleServiceImpl extends BaseServiceImp<ExamRule> implements ExamRuleService {
	private final ExamRuleDao examRuleDao;

	@Override
	public RBaseDao<ExamRule> getDao() {
		return examRuleDao;
	}

	@Override
	public List<ExamRule> getList(Integer examId) {
		return examRuleDao.getList(examId);
	}

	@Override
	public void clear(Integer examId) {
		examRuleDao.clear(examId);
	}
}