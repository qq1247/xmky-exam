package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyExerTrackDao;
import com.wcpdoc.exam.core.entity.MyExerTrack;
import com.wcpdoc.exam.core.service.MyExerTrackService;

/**
 * 我的练习跟踪服务层实现
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
@Service
public class MyExerTrackServiceImpl extends BaseServiceImp<MyExerTrack> implements MyExerTrackService {
	@Resource
	private MyExerTrackDao myExerTrackDao;

	@Override
	public RBaseDao<MyExerTrack> getDao() {
		return myExerTrackDao;
	}

	@Override
	public MyExerTrack getMyExerTrack(Integer exerId, Integer userId, Integer ymd) {
		return myExerTrackDao.getMyExerTrack(exerId, userId, ymd);
	}

	@Override
	public List<MyExerTrack> getList(Integer exerId, Integer userId, Integer startDate, Integer endDate) {
		return myExerTrackDao.getList(exerId, userId, startDate, endDate);
	}
}
