package com.wcpdoc.exam.base.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.dao.ParmDao;
import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.base.service.ParmExService;
import com.wcpdoc.exam.base.service.ParmService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;

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

}
