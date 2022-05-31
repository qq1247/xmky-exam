package com.wcpdoc.exam.core.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.BulletinDao;
import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.service.BulletinService;
import com.wcpdoc.file.service.FileService;

/**
 * 公告服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class BulletinServiceImpl extends BaseServiceImp<Bulletin> implements BulletinService {
	@Resource
	private BulletinDao bulletinDao;
	@Resource
	private FileService fileService;

	@Override
	@Resource(name = "bulletinDaoImpl")
	public void setDao(BaseDao<Bulletin> dao) {
		super.dao = dao;
	}
	

	@Override
	public void addAndUpdate(Bulletin bulletin) {
		if (ValidateUtil.isValid(bulletin.getReadUserIds())) {
			bulletin.setReadUserIds(","+bulletin.getReadUserIds()+",");
		}
		bulletin.setState(1);
		bulletin.setUpdateTime(new Date());
		bulletin.setUpdateUserId(getCurUser().getId());
		bulletinDao.add(bulletin);
		if (ValidateUtil.isValid(bulletin.getImgFileId())) {
			fileService.upload(Integer.parseInt(bulletin.getImgFileId()));
		}
	}

	@Override
	public void updateAndUpdate(Bulletin bulletin) {
		Bulletin entity = bulletinDao.getEntity(bulletin.getId());
		String oldImgFileId = entity.getImgFileId();
		entity.setTitle(bulletin.getTitle());
		entity.setImgFileId(bulletin.getImgFileId());
		entity.setContent(bulletin.getContent());
		if (ValidateUtil.isValid(bulletin.getReadUserIds())) {
			entity.setReadUserIds(","+bulletin.getReadUserIds()+",");
		}else{
			entity.setReadUserIds(null);
		}
		entity.setShowType(bulletin.getShowType());
		entity.setStartTime(bulletin.getStartTime());
		entity.setEndTime(bulletin.getEndTime());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		bulletinDao.update(entity);
		if (ValidateUtil.isValid(bulletin.getImgFileId()) && !bulletin.getImgFileId().equals(oldImgFileId) ) {
			fileService.upload(Integer.parseInt(bulletin.getImgFileId()));
		}
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
