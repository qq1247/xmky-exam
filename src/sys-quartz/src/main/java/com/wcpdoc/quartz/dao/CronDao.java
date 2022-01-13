package com.wcpdoc.quartz.dao;

import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.quartz.entity.Cron;

/**
 * 定时任务数据访问层接口
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
public interface CronDao extends RBaseDao<Cron> {
	/**
	 * 获取定时任务列表
	 * 
	 * v1.0 zhanghc 2019年9月13日下午7:10:39
	 * @return List<Cron>
	 */
	List<Cron> getList();

}
