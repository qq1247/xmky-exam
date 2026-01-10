package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionBankDao;
import com.wcpdoc.exam.core.entity.QuestionBank;
import com.wcpdoc.exam.core.service.QuestionBankExService;
import com.wcpdoc.exam.core.service.QuestionBankService;
import com.wcpdoc.exam.core.util.QuestionBankUtil;
import com.wcpdoc.file.service.FileService;
import com.wcpdoc.search.service.DocIndexService;

import lombok.RequiredArgsConstructor;

/**
 * 题库服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
@RequiredArgsConstructor
public class QuestionBankServiceImpl extends BaseServiceImp<QuestionBank> implements QuestionBankService {
	private final QuestionBankDao questionBankDao;
	private final QuestionBankExService questionBankExService;
	private final FileService fileService;
	private final DocIndexService searchService;

	@Override
	public RBaseDao<QuestionBank> getDao() {
		return questionBankDao;
	}

	@Override
	public void del(Integer id) {
		// 数据校验
		QuestionBank entity = getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| QuestionBankUtil.hasWrite(entity))) {
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
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| QuestionBankUtil.hasWrite(entity))) {
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

	@Override
	public void docAdd(Integer id, Integer fileId) {
		// 数据校验
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(fileId)) {
			throw new MyException("参数错误：fileId");
		}
		QuestionBank entity = getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| QuestionBankUtil.hasWrite(entity))) {
			throw new MyException("无操作权限");
		}

		// 附件保存
		fileService.upload(fileId);

		// 题库关联附件
		QuestionBank questionBank = getById(id);
		questionBank.getFileIds().add(fileId);
		updateById(questionBank);

		// 添加索引支持检索
		searchService.add(questionBank.getId(), fileId);
	}

	@Override
	public void docDel(Integer id, Integer fileId) {
		// 数据校验
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(fileId)) {
			throw new MyException("参数错误：fileId");
		}
		QuestionBank entity = getById(id);
		if (!(CurLoginUserUtil.isSelf(entity.getCreateUserId()) || CurLoginUserUtil.isAdmin()
				|| QuestionBankUtil.hasWrite(entity))) {
			throw new MyException("无操作权限");
		}

		// 题库关联附件删除
		QuestionBank questionBank = getById(id);
		questionBank.getFileIds().remove(fileId);
		updateById(questionBank);

		// 删除相关索引
		searchService.del(entity.getId(), fileId);
	}

	@Override
	public List<QuestionBank> getList() {
		return questionBankDao.getList();
	}
}
