package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
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
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	
	@Override
	public void setDao(BaseDao<QuestionType> dao) {
	}

	@Override
	public void delEx(QuestionType questionType) {
		PageIn pageIn = new PageIn().setPageSize(1).addAttr("questionTypeId", questionType.getId().toString());
		int questionNum = questionService.getListpage(pageIn).getTotal();
		if (questionNum > 0) {
			throw new MyException("该题库有试题，不允许删除");
		}
	}

	@Override
	public void move(Integer sourceId, Integer targetId) {
		// 校验数据有效性
		if (sourceId == null) {
			throw new MyException("参数错误：sourceId");
		}
		if(targetId == null){
			throw new MyException("参数错误：targetId");
		}
		
		QuestionType source = questionTypeService.getEntity(sourceId);
		if (source == null){
			throw new MyException(String.format("参数错误：sourceId"));
		}
		QuestionType target = questionTypeService.getEntity(targetId);
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
			//question.setUpdateTime(new Date());
			//question.setUpdateUserId(getCurUser().getId()); // 保留移动前的
			questionService.update(question);
		}
	}
	
	@Override
	public void clear(Integer id) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(id)) {
			throw new MyException("参数错误：id");
		}
		QuestionType entity = getEntity(id);
		if (entity.getUpdateUserId().intValue() != getCurUser().getId()) {
			throw new MyException("无操作权限");
		}
		
		// 题库清空
		List<Question> questionList = questionService.getList(id);
		for (Question question : questionList) {
			if (question.getState() == 0) {
				continue;
			}
			question.setState(0);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			questionService.update(question);
		}
	}
}
