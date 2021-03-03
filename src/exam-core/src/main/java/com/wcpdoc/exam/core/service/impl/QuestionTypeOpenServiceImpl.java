package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.QuestionTypeOpenDao;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;
import com.wcpdoc.exam.core.service.QuestionTypeOpenService;

/**
 * 试题分类服务层实现
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
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		QuestionTypeOpen entity = getEntity(id);
		entity.setState(0);
		update(entity);
	}
}
