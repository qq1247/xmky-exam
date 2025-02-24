package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
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
	public void delEx(Integer id) {
		// 数据校验
		QuestionBank entity = getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
			throw new MyException("无操作权限");
		}
		// 题库删除
		// entity.setUpdateTime(new Date());// 物理删除，不需要再记录
		// entity.setUpdateUserId(getCurUser().getId());
		removeById(entity.getId());

		// 题库扩展删除
		questionBankExService.delEx(entity);
	}

	@Override
	public void move(Integer sourceId, Integer targetId) {
		questionBankExService.move(sourceId, targetId);
	}

	@Override
	public void clear(Integer id) {
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
