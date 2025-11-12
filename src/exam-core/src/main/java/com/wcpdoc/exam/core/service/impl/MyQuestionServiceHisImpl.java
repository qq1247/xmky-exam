package com.wcpdoc.exam.core.service.impl;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyQuestionHisDao;
import com.wcpdoc.exam.core.entity.MyExamQuestionHis;
import com.wcpdoc.exam.core.service.MyQuestionHisService;

import lombok.RequiredArgsConstructor;

/**
 * 我的试题服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
@RequiredArgsConstructor
public class MyQuestionServiceHisImpl extends BaseServiceImp<MyExamQuestionHis> implements MyQuestionHisService {
	private final MyQuestionHisDao myQuestionHisDao;

	@Override
	public RBaseDao<MyExamQuestionHis> getDao() {
		return myQuestionHisDao;
	}

}
