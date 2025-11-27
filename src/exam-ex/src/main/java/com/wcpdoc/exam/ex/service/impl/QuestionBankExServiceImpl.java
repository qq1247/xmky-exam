package com.wcpdoc.exam.ex.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.QuestionBankExService;
import com.wcpdoc.exam.core.service.QuestionService;

import lombok.RequiredArgsConstructor;

/**
 * 题库扩展服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
@RequiredArgsConstructor
public class QuestionBankExServiceImpl extends BaseServiceImp<QuestionBank> implements QuestionBankExService {

	@Override
	public RBaseDao<QuestionBank> getDao() {
		return null;
	}

	@Override
	public void del(QuestionBank questionBank) {
		List<Integer> questionIds = SpringUtil.getBean(QuestionService.class).getIds(questionBank.getId());
		if (ValidateUtil.isValid(questionIds)) {
			throw new MyException("请先清空试题");
		}
	}

	@Override
	public void clear(Integer id) {
		// 题库清空
		List<Question> questionList = SpringUtil.getBean(QuestionService.class).getList(id);
		for (Question question : questionList) {
			question.setState(0);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			SpringUtil.getBean(QuestionService.class).updateById(question);
		}
	}

}
