package com.wcpdoc.exam.wordFilter.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.startx.http.wordfilter.WordContext;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.SpringUtil;
import com.wcpdoc.exam.wordFilter.dao.SensitiveDao;
import com.wcpdoc.exam.wordFilter.entity.Sensitive;
import com.wcpdoc.exam.wordFilter.service.SensitiveService;
import com.wcpdoc.exam.wordFilter.util.SensitiveUtil;

/**
 * 公告服务层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Service
public class SensitiveServiceImpl extends BaseServiceImp<Sensitive> implements SensitiveService {
	@Resource
	private SensitiveDao sensitiveDao;
    
	@Override
	@Resource(name = "sensitiveDaoImpl")
	public void setDao(BaseDao<Sensitive> dao) {
		super.dao = dao;
	}

	@Override
	public void updateAndUpdate(Sensitive sensitive) {
		Sensitive entity;
		if (sensitive.getId() == null) { //添加
			entity = new Sensitive();
			entity.setBlackList(sensitive.getBlackList());
			entity.setWhiteList(sensitive.getWhiteList());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			sensitiveDao.add(entity);
		}else{			                 //修改
			entity = sensitiveDao.getEntity(sensitive.getId());
			entity.setBlackList(sensitive.getBlackList());
			entity.setWhiteList(sensitive.getWhiteList());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			sensitiveDao.update(entity);
		}
		
		// 初始化
		SpringUtil.getBean(WordContext.class).initKeyWord(entity);
	}

	@Override
	public String replace(String content) {
		return SensitiveUtil.replace(content);
	}
}
