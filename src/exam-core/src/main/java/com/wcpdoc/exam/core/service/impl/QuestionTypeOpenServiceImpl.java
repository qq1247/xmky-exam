package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.QuestionTypeOpenDao;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionTypeOpenService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试题分类开放服务层实现
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Service
public class QuestionTypeOpenServiceImpl extends BaseServiceImp<QuestionTypeOpen> implements QuestionTypeOpenService {
	@Resource
	private QuestionTypeOpenDao questionTypeOpenDao;

	@Override
	@Resource(name = "questionTypeOpenDaoImpl")
	public void setDao(BaseDao<QuestionTypeOpen> dao) {
		super.dao = dao;
	}
	
	@Override
	public void addAndUpdate(QuestionTypeOpen questionTypeOpen) {
		// 校验数据有效性
		List<QuestionTypeOpen> list = questionTypeOpenDao.getList(questionTypeOpen.getStartTime(), questionTypeOpen.getEndTime(), questionTypeOpen.getQuestionTypeId());
		if (list.size() != 0) {
			throw new MyException("时间有误！");
		}
		
		if (ValidateUtil.isValid(questionTypeOpen.getUserIds())) {
			questionTypeOpen.setUserIds(","+questionTypeOpen.getUserIds()+","+getCurUser().getId()+",");
		}
		if (ValidateUtil.isValid(questionTypeOpen.getOrgIds())) {
			questionTypeOpen.setOrgIds(","+questionTypeOpen.getOrgIds()+",");
		}
		questionTypeOpen.setUpdateUserId(getCurUser().getId());
		questionTypeOpen.setUpdateTime(new Date());
		questionTypeOpen.setState(1);
		questionTypeOpenDao.add(questionTypeOpen);
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		QuestionTypeOpen entity = getEntity(id);
		entity.setState(2);
		update(entity);
	}
}
