package com.wcpdoc.exam.core.service.impl;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyExamHisDao;
import com.wcpdoc.exam.core.entity.MyExamHis;
import com.wcpdoc.exam.core.service.MyExamHisService;

import lombok.RequiredArgsConstructor;

/**
 * 我的考试历史服务层实现
 * 
 * v1.0 zhanghc 2025年7月12日下午1:53:07
 */
@Service
@RequiredArgsConstructor
public class MyExamHisServiceImpl extends BaseServiceImp<MyExamHis> implements MyExamHisService {

	private final MyExamHisDao myExamHisDao;

	@Override
	public RBaseDao<MyExamHis> getDao() {
		return myExamHisDao;
	}
}
