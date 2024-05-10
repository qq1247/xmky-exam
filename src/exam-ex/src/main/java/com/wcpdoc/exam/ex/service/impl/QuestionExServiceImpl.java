package com.wcpdoc.exam.ex.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.service.QuestionExService;

/**
 * 试题扩展服务层实现
 * 
 * v1.0 zhanghc 2022年9月6日上午9:54:24
 */
@Service
public class QuestionExServiceImpl extends BaseServiceImp<Question> implements QuestionExService {
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private ExamCacheService examCacheService;

	@Override
	public RBaseDao<Question> getDao() {
		return null;
	}

	@Override
	public void updateValid(Question question) {
		List<Integer> examIdList = myQuestionService.getExamIdList(question.getId());
		if (ValidateUtil.isValid(examIdList)) {
			Exam exam = examCacheService.getExam(examIdList.get(0));
			throw new MyException(String.format("试题已被【%s】考试使用", exam.getName()));
		}
	}
}
