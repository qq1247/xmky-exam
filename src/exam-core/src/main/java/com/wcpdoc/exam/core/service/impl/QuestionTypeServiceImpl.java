package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionTypeDao;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionTypeExService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
/**
 * 题库服务层实现
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Service
public class QuestionTypeServiceImpl extends BaseServiceImp<QuestionType> implements QuestionTypeService {
	@Resource
	private QuestionTypeDao questionTypeDao;
	@Resource
	private QuestionTypeExService questionTypeExService;

	@Override
	@Resource(name = "questionTypeDaoImpl")
	public void setDao(BaseDao<QuestionType> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(QuestionType questionType) {
		//校验数据有效性
		if (!ValidateUtil.isValid(questionType.getName())) {
			throw new MyException("参数错误：name");
		}
		//if (existName(questionType)) {
		//	throw new MyException("名称已存在");
		//} // 不同的子管理员添加可以重复
		
		// 添加题库
		questionType.setUpdateTime(new Date());
		questionType.setUpdateUserId(getCurUser().getId());
		add(questionType);
	}

	@Override
	public void editAndUpdate(QuestionType questionType) {
		//校验数据有效性
		if(!ValidateUtil.isValid(questionType.getName())) {
			throw new MyException("参数错误：name");
		}
		QuestionType entity = getEntity(questionType.getId());
		if (entity.getUpdateUserId().intValue() != getCurUser().getId()) {
			throw new MyException("无操作权限");
		}
		
		// 保存题库
		entity.setName(questionType.getName());
		entity.setUpdateTime(new Date());
		update(entity);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		//校验数据有效性
		QuestionType entity = getEntity(id);
		if (entity.getUpdateUserId().intValue() != getCurUser().getId()) {
			throw new MyException("无操作权限");
		}
		// 删除题库
		del(entity.getId());
		
		// 删除题库扩展
		questionTypeExService.delAndUpdate(entity);
	}

	@Override
	public void move(Integer sourceId, Integer targetId) {
		questionTypeExService.move(sourceId, targetId);
	}
}
