package com.wcpdoc.exam.ex.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.QuestionExService;

/**
 * 试题扩展服务层实现
 * 
 * v1.0 zhanghc 2022年9月6日上午9:54:24
 */
@Service
public class QuestionExServiceImpl extends BaseServiceImp<Question> implements QuestionExService {
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	@Lazy
	private ExamService examService;

	@Override
	public RBaseDao<Question> getDao() {
		return null;
	}

	@Override
	public void updateValid(Question question) {
		List<ExamQuestion> examQuestionList = examQuestionService.getList1(question.getId());
		if (ValidateUtil.isValid(examQuestionList)) {
			Exam exam = examService.getById(examQuestionList.get(0).getExamId());
			throw new MyException(String.format("试题已被【考试：%s】使用", exam.getName()));
		}
	}
}
