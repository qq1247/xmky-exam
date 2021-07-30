package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.BulletinDao;
import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BulletinService;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
	@Resource(name = "bulletinDaoImpl")
	public void setDao(BaseDao<Bulletin> dao) {
		super.dao = dao;
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		Bulletin entity = getEntity(id);
		entity.setState(0);
		update(entity);
	}

	@Override
	public void auth(Integer id, String readUserIds, String readOrgIds) {		
		Bulletin entity = getEntity(id);
		if (!ValidateUtil.isValid(readUserIds)) {
			entity.setReadUserIds(null);
		} else {
			entity.setReadUserIds(readUserIds);
		}
		if (!ValidateUtil.isValid(readOrgIds)) {
			entity.setReadOrgIds(null);
		} else {
			entity.setReadOrgIds(readOrgIds);
		}
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		update(entity);
	}

	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		return bulletinDao.getUserListpage(pageIn);
	}

	@Override
	public PageOut getOrgListpage(PageIn pageIn) {
		return bulletinDao.getOrgListpage(pageIn);
	}
}
