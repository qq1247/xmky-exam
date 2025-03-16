package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MySxeDao;
import com.wcpdoc.exam.core.entity.MySxe;
import com.wcpdoc.exam.core.service.MySxeService;

/**
 * 我的作弊服务层实现
 * 
 * v1.0 zhanghc 2025年3月16日上午11:21:42
 */
@Service
public class MySxeServiceImpl extends BaseServiceImp<MySxe> implements MySxeService {

	@Resource
	private MySxeDao mySxeDao;

	@Override
	public RBaseDao<MySxe> getDao() {
		return mySxeDao;
	}

	@Override
	public List<MySxe> getList(Integer examId, Integer userId) {
		return mySxeDao
				.selectList(new LambdaQueryWrapper<MySxe>().eq(MySxe::getExamId, examId).eq(MySxe::getUserId, userId));
	}
}
