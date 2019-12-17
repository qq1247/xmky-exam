package com.wcpdoc.exam.quartz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.Job;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.quartz.dao.CronDao;
import com.wcpdoc.exam.quartz.entity.Cron;
import com.wcpdoc.exam.quartz.service.CronService;
import com.wcpdoc.exam.quartz.util.QuartzUtil;

/**
 * 定时任务服务层实现
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
@Service
public class CronServiceImpl extends BaseServiceImp<Cron> implements CronService {
	@Resource
	private CronDao cronDao;

	@Override
	@Resource(name = "cronDaoImpl")
	public void setDao(BaseDao<Cron> dao) {
		super.dao = dao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addAndUpdate(Cron cron) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(cron.getJobClass())) {
			throw new RuntimeException("无法获取参数：jobClass");
		}
		if (!ValidateUtil.isValid(cron.getCron())) {
			throw new RuntimeException("无法获取参数：cron");
		}

		Class<Job> jobClass = null;
		try {
			jobClass = (Class<Job>) Class.forName(cron.getJobClass());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("无法实例化：" + cron.getJobClass());
		}
		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new RuntimeException("未实现Job接口：" + cron.getJobClass());
		}
		if (!QuartzUtil.validExpression(cron.getCron())) {
			throw new RuntimeException("cron表达式错误：" + cron.getCron());
		}

		// 添加定时任务，默认不启动
		cron.setState(2);
		cronDao.add(cron);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateAndUpdate(Cron cron) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(cron.getJobClass())) {
			throw new RuntimeException("无法获取参数：jobClass");
		}
		if (!ValidateUtil.isValid(cron.getCron())) {
			throw new RuntimeException("无法获取参数：cron");
		}

		Class<Job> jobClass = null;
		try {
			jobClass = (Class<Job>) Class.forName(cron.getJobClass());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("无法实例化：" + cron.getJobClass());
		}
		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new RuntimeException("未实现Job接口：" + cron.getJobClass());
		}
		if (!QuartzUtil.validExpression(cron.getCron())) {
			throw new RuntimeException("cron表达式错误：" + cron.getCron());
		}

		// 修改定时任务，重置为不启动
		cron.setState(2);
		cronDao.update(cron);

		// 删除作业
		QuartzUtil.deleteJob(cron.getId());
	}

	@Override
	public void delAndUpdate(Integer[] ids) {
		// 删除作业
		for (Integer id : ids) {
			Cron cron = getEntity(id);
			QuartzUtil.deleteJob(cron.getId());
		}
		
		// 删除实体
		del(ids);
	}

	@Override
	public List<Cron> getList() {
		return cronDao.getList();
	}
}
