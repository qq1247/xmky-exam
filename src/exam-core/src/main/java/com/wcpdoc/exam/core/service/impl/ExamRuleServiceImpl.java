package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.ExamRuleDao;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 考试随机规则服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class ExamRuleServiceImpl extends BaseServiceImp<ExamRule> implements ExamRuleService {
	@Resource
	private ExamRuleDao examRuleDao;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private QuestionService questionService;
	@Resource
	private ExamService examService;
	@Resource
	private ExamQuestionService examQuestionService;
	
	@Override
	@Resource(name = "examRuleDaoImpl")
	public void setDao(BaseDao<ExamRule> dao) {
		super.dao = dao;
	}
	
	@Override
	public List<ExamRule> getList(Integer examId) {
		return examRuleDao.getList(examId);
	}
}