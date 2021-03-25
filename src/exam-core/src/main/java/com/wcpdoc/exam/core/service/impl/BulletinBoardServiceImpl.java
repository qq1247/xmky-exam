package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.BulletinBoardDao;
import com.wcpdoc.exam.core.entity.BulletinBoard;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BulletinBoardService;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 公告栏服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class BulletinBoardServiceImpl extends BaseServiceImp<BulletinBoard> implements BulletinBoardService {
	@Resource
	private BulletinBoardDao bulletinBoardDao;

	@Override
	@Resource(name = "bulletinBoardDaoImpl")
	public void setDao(BaseDao<BulletinBoard> dao) {
		super.dao = dao;
	}
	
	@Override
	public void delAndUpdate(Integer id) {
		// 校验数据有效性
		BulletinBoard entity = getEntity(id);
		entity.setState(0);
		update(entity);
	}

	@Override
	public void auth(Integer id, String readUserIds, String readOrgIds) {		
		BulletinBoard entity = getEntity(id);
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
		return bulletinBoardDao.getUserListpage(pageIn);
	}

	@Override
	public PageOut getOrgListpage(PageIn pageIn) {
		return bulletinBoardDao.getOrgListpage(pageIn);
	}
}
