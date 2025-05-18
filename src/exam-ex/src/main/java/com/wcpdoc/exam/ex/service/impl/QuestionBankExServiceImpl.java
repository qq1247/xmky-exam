package com.wcpdoc.exam.ex.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionBankExService;
import com.wcpdoc.exam.core.service.QuestionBankService;

/**
 * 题库扩展服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
public class QuestionBankExServiceImpl extends BaseServiceImp<QuestionBank> implements QuestionBankExService {

	@Resource
	@Lazy
	private QuestionService questionService;
	@Resource
	@Lazy
	private QuestionBankService questionBankService;

	@Override
	public RBaseDao<QuestionBank> getDao() {
		return null;
	}

	@Override
	public void delEx(QuestionBank questionBank) {
		List<Integer> questionIds = questionService.getIds(questionBank.getId());
		if (ValidateUtil.isValid(questionIds)) {
			throw new MyException("请先清空试题");
		}
	}

	@Override
	public void clear(Integer id) {
		// 数据校验
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		QuestionBank entity = questionBankService.getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}

		// 题库清空
		List<Question> questionList = questionService.getList(id);
		for (Question question : questionList) {
			question.setState(0);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			questionService.updateById(question);
		}
	}

}
