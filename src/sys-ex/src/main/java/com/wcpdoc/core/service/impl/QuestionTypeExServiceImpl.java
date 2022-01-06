package com.wcpdoc.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.exception.MyException;
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
		PageIn pageIn = new PageIn().setPageSize(1).addAttr("paperId", questionType.getId());
		int questionNum = questionService.getListpage(pageIn).getTotal();
		if (questionNum > 0) {
			throw new MyException("该分类下有试题，不允许删除");
		}
	}
}
