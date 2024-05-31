package com.wcpdoc.quartz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.BaseCacheService;
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
	@Resource
	private BaseCacheService baseCacheService;
	@Override
	public RBaseDao<Cron> getDao() {
		return null;
	}

	@Override
	public String getDbBakDir() {
		return baseCacheService.getParm().getDbBakDir();
	}
}
