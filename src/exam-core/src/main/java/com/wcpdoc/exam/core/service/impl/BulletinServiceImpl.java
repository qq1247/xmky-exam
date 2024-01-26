package com.wcpdoc.exam.core.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.BulletinDao;
import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.service.BulletinService;

/**
 * 公告服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class BulletinServiceImpl extends BaseServiceImp<Bulletin> implements BulletinService {
	@Resource
	private BulletinDao bulletinDao;

	@Override
	public RBaseDao<Bulletin> getDao() {
		return bulletinDao;
	}
}
