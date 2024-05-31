package com.wcpdoc.quartz.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.quartz.entity.Cron;

/**
 * 定时任务服务层接口
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
public interface CronService extends BaseService<Cron> {

	/**
	 * 定时任务修改
	 * 
	 * v1.0 zhanghc 2019年9月11日下午4:38:46
	 * 
	 * @param cron void
	 */
	void updateEx(Cron cron);

	/**
	 * 定时任务删除
	 * 
	 * v1.0 zhanghc 2019年9月11日下午4:38:46
	 * 
	 * @param id void
	 */
	void delEx(Integer id);

	/**
	 * 定时任务列表
	 * 
	 * v1.0 zhanghc 2019年9月13日下午7:10:39
	 * 
	 * @return List<Cron>
	 */
	List<Cron> getList();

	/**
	 * 任务启动
	 * 
	 * v1.0 zhanghc 2020年8月25日下午4:01:47
	 * 
	 * @param id void
	 */
	void startTask(Integer id);

	/**
	 * 任务停止
	 * 
	 * v1.0 zhanghc 2020年8月25日下午4:08:24
	 * 
	 * @param id void
	 */
	void stopTask(Integer id);

	/**
	 * 执行一次
	 * 
	 * v1.0 zhanghc 2020年8月25日下午4:10:49
	 * 
	 * @param id void
	 */
	void runOnceTask(Integer id);

}
