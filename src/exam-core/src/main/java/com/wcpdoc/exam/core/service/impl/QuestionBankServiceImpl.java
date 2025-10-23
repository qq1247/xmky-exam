package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionBankDao;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.QuestionBankExService;
import com.wcpdoc.exam.core.service.QuestionBankService;

/**
 * 题库服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
public class QuestionBankServiceImpl extends BaseServiceImp<QuestionBank> implements QuestionBankService {
	@Resource
	private QuestionBankDao questionBankDao;
	@Resource
	private QuestionBankExService questionBankExService;
	@Resource
	private QuestionDao questionDao;

	@Override
	public RBaseDao<QuestionBank> getDao() {
		return questionBankDao;
	}

	@Override
	public void del(Integer id) {
		// 数据校验
		QuestionBank entity = getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}

		// 题库删除
		entity.setObjectiveNum(0);
		entity.setSubjectiveNum(0);
		entity.setSingleNum(0);
		entity.setMultipleNum(0);
		entity.setFillBlankObjNum(0);
		entity.setFillBlankSubNum(0);
		entity.setJudgeNum(0);
		entity.setQaObjNum(0);
		entity.setQaSubNum(0);
		entity.setQuestionNum(0);
		entity.setState(0);// bug：直接删除会导致练习引用为空
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		updateById(entity);

		// 题库扩展删除
		questionBankExService.del(entity);
	}

	@Override
	public void clear(Integer id) {
		// 数据校验
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		QuestionBank entity = getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		// 题库清理
		QuestionBank questionBank = getById(id);
		questionBank.setQuestionNum(0);
		questionBank.setObjectiveNum(0);
		questionBank.setSubjectiveNum(0);
		questionBank.setSingleNum(0);
		questionBank.setMultipleNum(0);
		questionBank.setFillBlankObjNum(0);
		questionBank.setFillBlankSubNum(0);
		questionBank.setJudgeNum(0);
		questionBank.setQaObjNum(0);
		questionBank.setQaSubNum(0);
		updateById(questionBank);

		// 题库扩展清理
		questionBankExService.clear(id);
	}

}
