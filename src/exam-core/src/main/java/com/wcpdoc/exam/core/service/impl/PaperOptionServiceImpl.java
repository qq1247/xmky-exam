package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.PaperOptionDao;
import com.wcpdoc.exam.core.entity.PaperOption;
import com.wcpdoc.exam.core.service.PaperOptionService;

/**
 * 试卷服务层实现
 * 
 * v1.0 chenyun 2021-03-10 13:47:35
 */
@Service
public class PaperOptionServiceImpl extends BaseServiceImp<PaperOption> implements PaperOptionService {
	@Resource
	private PaperOptionDao paperOptionDao;

	@Override
	@Resource(name = "paperOptionDaoImpl")
	public void setDao(BaseDao<PaperOption> dao) {
		super.dao = dao;
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		PaperOption entity = getEntity(id);
		update(entity);
	}
}
