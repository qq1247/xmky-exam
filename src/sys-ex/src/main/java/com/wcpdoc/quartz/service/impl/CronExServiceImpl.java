package com.wcpdoc.quartz.service.impl;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.quartz.entity.Cron;
import com.wcpdoc.quartz.service.CronExService;

/**
 * 定时任务扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
public class CronExServiceImpl extends BaseServiceImp<Cron> implements CronExService {
	@Override
	public RBaseDao<Cron> getDao() {
		return null;
	}

	@Override
	public String getDbBakDir() {
		return ParmCache.get().getDbBakDir();
	}
}
