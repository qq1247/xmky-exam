package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeExService;

/**
 * 试题分类扩展服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
public class QuestionTypeExServiceImpl implements QuestionTypeExService {

	@Resource
	private QuestionService questionService;

	@Override
	public void delAndUpdate(QuestionType questionType) {
		List<Question> questionList = questionService.getList(questionType.getId());
		for (Question question : questionList) {
			question.setQuestionTypeId(1);
		}
	}
}
