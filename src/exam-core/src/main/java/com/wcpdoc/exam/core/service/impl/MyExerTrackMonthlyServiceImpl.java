package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyExerTrackMonthlyDao;
import com.wcpdoc.exam.core.entity.MyExerTrackMonthly;
import com.wcpdoc.exam.core.service.MyExerTrackMonthlyService;

/**
 * 我的练习跟踪月度服务层实现
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
@Service
public class MyExerTrackMonthlyServiceImpl extends BaseServiceImp<MyExerTrackMonthly>
		implements MyExerTrackMonthlyService {
	@Resource
	private MyExerTrackMonthlyDao myExerTrackMonthlyDao;

	@Override
	public RBaseDao<MyExerTrackMonthly> getDao() {
		return myExerTrackMonthlyDao;
	}

	@Override
	public MyExerTrackMonthly getMyExerTrackMonthly(Integer exerId, Integer userId, Integer ym) {
		return myExerTrackMonthlyDao.getMyExerTrackMonthly(exerId, userId, ym);
	}

	@Override
	public List<MyExerTrackMonthly> getList(Integer exerId, Integer userId, Integer startYm, Integer endYm) {
		return myExerTrackMonthlyDao.getList(exerId, userId, startYm, endYm);
	}
}
