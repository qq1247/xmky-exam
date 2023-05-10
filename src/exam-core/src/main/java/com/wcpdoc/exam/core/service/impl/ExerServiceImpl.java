package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExerDao;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.service.ExerRmkService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;

/**
 * 练习服务层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Service
public class ExerServiceImpl extends BaseServiceImp<Exer> implements ExerService {
	@Resource
	private ExerDao exerDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private ExerRmkService exerRmkService;

	@Override
	@Resource(name = "exerDaoImpl")
	public void setDao(BaseDao<Exer> dao) {
		super.dao = dao;
	}

	@Override
	public void addEx(Exer exer) {
		// 数据有效性校验
		if (!ValidateUtil.isValid(exer.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exer.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(exer.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exer.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		if (!ValidateUtil.isValid(exer.getRmkState())) {
			throw new MyException("参数错误：rmkState");
		}
		
		List<Exer> exerList = exerDao.getList(exer.getQuestionTypeId());
		for (Exer cur : exerList) {
			if (exer.getStartTime().getTime() <= cur.getStartTime().getTime() && cur.getStartTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() <= cur.getEndTime().getTime() && cur.getEndTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() >= cur.getStartTime().getTime() && cur.getEndTime().getTime() >= exer.getEndTime().getTime() ) {
				throw new MyException("该时间段已存在");
			}
		}
		
		// 练习添加
		exer.setState(1);
		exer.setUpdateUserId(getCurUser().getId());
		exer.setUpdateTime(new Date());
		add(exer);
	}
	
	@Override
	public void updateEx(Exer exer) {
		// 数据有效性校验
		if (!ValidateUtil.isValid(exer.getId())) {
			throw new MyException("参数错误：id");
		}
		if (!ValidateUtil.isValid(exer.getName())) {
			throw new MyException("参数错误：name");
		}
		if (!ValidateUtil.isValid(exer.getQuestionTypeId())) {
			throw new MyException("参数错误：questionTypeId");
		}
		if (!ValidateUtil.isValid(exer.getStartTime())) {
			throw new MyException("参数错误：startTime");
		}
		if (!ValidateUtil.isValid(exer.getEndTime())) {
			throw new MyException("参数错误：endTime");
		}
		if (!ValidateUtil.isValid(exer.getRmkState())) {
			throw new MyException("参数错误：rmkState");
		}
		Exer entity = getEntity(exer.getId());
		if (entity.getState() == 0) {
			throw new MyException("已删除");
		}
		if (entity.getEndTime().getTime() <= System.currentTimeMillis()) {
			throw new MyException("练习已结束");
		}
		if (entity.getStartTime().getTime() <= System.currentTimeMillis()) {
			throw new MyException("练习已开始");// 考试结束放在考试开始前校验，可能的问题为考试已结束，提示的是考试已开始。
		}
		
		List<Exer> exerList = exerDao.getList(exer.getQuestionTypeId());
		for (Exer cur : exerList) {
			if (cur.getId().intValue() == exer.getId().intValue()) {// 如果切换题库，该行无效；如果同一个题库，校验的时候排除自己。
				continue;
			}
			if (exer.getStartTime().getTime() <= cur.getStartTime().getTime() && cur.getStartTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() <= cur.getEndTime().getTime() && cur.getEndTime().getTime() <= exer.getEndTime().getTime()) {
				throw new MyException("该时间段已存在");
			}
			if (exer.getStartTime().getTime() >= cur.getStartTime().getTime() && cur.getEndTime().getTime() >= exer.getEndTime().getTime() ) {
				throw new MyException("该时间段已存在");
			}
		}
		
		// 练习修改
		entity.setName(exer.getName());
		entity.setQuestionTypeId(exer.getQuestionTypeId());
		entity.setStartTime(exer.getStartTime());
		entity.setEndTime(exer.getEndTime());
		entity.setUserIds(exer.getUserIds());
		//entity.setState(exer.getState());
		entity.setRmkState(exer.getRmkState());
		entity.setUpdateUserId(getCurUser().getId());
		entity.setUpdateTime(new Date());
		update(entity);
	}

	@Override
	public List<Exer> getList(Integer questionTypeId) {
		return exerDao.getList(questionTypeId);
	}
}
