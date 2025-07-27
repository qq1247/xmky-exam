package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyQuestionHisDao;
import com.wcpdoc.exam.core.entity.MyQuestionHis;
import com.wcpdoc.exam.core.service.MyQuestionHisService;

/**
 * 我的试题服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyQuestionServiceHisImpl extends BaseServiceImp<MyQuestionHis> implements MyQuestionHisService {
	@Resource
	private MyQuestionHisDao myQuestionHisDao;

	@Override
	public RBaseDao<MyQuestionHis> getDao() {
		return myQuestionHisDao;
	}

}
