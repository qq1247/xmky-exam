package com.wcpdoc.exam.sys.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.exam.service.QuestionTypeExService;

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
		for(Question question : questionList){
			question.setQuestionTypeId(1);
		}
	}
}
