package com.wcpdoc.quartz.service.impl;

import org.springframework.stereotype.Service;

import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.quartz.entity.Cron;
import com.wcpdoc.quartz.service.CronExService;

import lombok.RequiredArgsConstructor;

/**
 * 定时任务扩展服务层实现
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
@Service
@RequiredArgsConstructor
public class CronExServiceImpl extends BaseServiceImp<Cron> implements CronExService {
	private final BaseCacheService baseCacheService;

	@Override
	public RBaseDao<Cron> getDao() {
		return null;
	}

	@Override
	public String getDbBakDir() {
		return baseCacheService.getParm().getDbBakDir();
	}
}
