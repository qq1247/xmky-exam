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
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 题库扩展服务层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Service
public class QuestionTypeExServiceImpl extends BaseServiceImp<QuestionType> implements QuestionTypeExService {

	@Resource
	@Lazy
	private QuestionService questionService;
	@Resource
	@Lazy
	private QuestionTypeService questionTypeService;

	@Override
	public RBaseDao<QuestionType> getDao() {
		return null;
	}

	@Override
	public void delEx(QuestionType questionType) {
		List<Integer> questionIds = questionService.getIds(questionType.getId());
		if (ValidateUtil.isValid(questionIds)) {
			throw new MyException("请先清空试题");
		}
	}

	@Override
	public void move(Integer sourceId, Integer targetId) {
		// 数据校验
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if (targetId == null) {
			throw new MyException("参数错误：targetId");
		}

		QuestionType source = questionTypeService.getById(sourceId);
		if (source == null) {
			throw new MyException(String.format("参数错误：sourceId"));
		}
		QuestionType target = questionTypeService.getById(targetId);
		if (target == null) {
			throw new MyException(String.format("参数错误：targetId"));
		}

		if (source.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}
		if (target.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("无操作权限");
		}

		// 合并
		List<Question> questionList = questionService.getList(sourceId);
		for (Question question : questionList) {
			question.setQuestionTypeId(targetId);
			// question.setUpdateTime(new Date());
			// question.setUpdateUserId(getCurUser().getId()); // 保留移动前的
			questionService.updateById(question);
		}
	}

	@Override
	public void clear(Integer id) {
		// 数据校验
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		QuestionType entity = questionTypeService.getById(id);
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
