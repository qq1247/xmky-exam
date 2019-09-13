package com.wcpdoc.exam.quartz.service;

import java.util.List;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.quartz.entity.Cron;

/**
 * 定时任务服务层接口
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
public interface CronService extends BaseService<Cron> {

	/**
	 * 添加定时任务
	 * 
	 * v1.0 zhanghc 2019年9月11日下午4:38:46
	 * @param cron 
	 * void
	 */
	void addAndUpdate(Cron cron);

	/**
	 * 修改定时任务
	 * 
	 * v1.0 zhanghc 2019年9月11日下午4:38:46
	 * @param cron 
	 * void
	 */
	void updateAndUpdate(Cron cron);

	/**
	 * 删除定时任务
	 * 
	 * v1.0 zhanghc 2019年9月11日下午4:38:46
	 * @param cron 
	 * void
	 */
	void delAndUpdate(Integer[] ids);

	/**
	 * 获取定时任务列表
	 * 
	 * v1.0 zhanghc 2019年9月13日下午7:10:39
	 * @return List<Cron>
	 */
	List<Cron> getList();

}
