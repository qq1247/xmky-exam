package com.wcpdoc.base.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.dao.ParmDao;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmExService;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;

/**
 * 参数服务层实现
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Service
public class ParmServiceImpl extends BaseServiceImp<Parm> implements ParmService {
	@Resource
	private ParmDao parmDao;
	@Resource
	private ParmExService parmExService;

	@Override
	@Resource(name = "parmDaoImpl")
	public void setDao(BaseDao<Parm> dao) {
		super.dao = dao;
	}

	@Override
	public void addAndUpdate(Parm parm) {
		// 添加参数
		add(parm);
		
		// 扩展处理
		parmExService.addAndUpdate(parm);
	}
	
	@Override
	public void updateAndUpdate(Parm parm) {
		// 添加参数
		update(parm);
		
		// 扩展处理
		parmExService.updateAndUpdate(parm);
	}

	@Override
	public Parm get() {
		if(parmDao.getList().size() == 0){
			return null;
		}
		return parmDao.getList().get(0);
	}

	@Override
	public void editLogo(Parm parm) throws Exception {
		if(parm.getId() == null){
			parm.setUpdateTime(new Date());
			parm.setUpdateUserId(getCurUser().getId());
			parmDao.add(parm);
			parmExService.ImageIcon(parm);
			return;
		}
		Parm entity = parmDao.getEntity(parm.getId());
		entity.setOrgLogo(parm.getOrgLogo());
		entity.setOrgName(parm.getOrgName());
		entity.setUpdateTime(new Date());
		entity.setUpdateUserId(getCurUser().getId());
		parmDao.update(entity);
		parmExService.ImageIcon(entity);
	}
}
