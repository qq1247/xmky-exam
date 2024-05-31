package com.wcpdoc.quartz.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.quartz.Job;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.quartz.dao.CronDao;
import com.wcpdoc.quartz.entity.Cron;
import com.wcpdoc.quartz.exception.QuartzException;
import com.wcpdoc.quartz.service.CronService;
import com.wcpdoc.quartz.util.QuartzUtil;

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
	public RBaseDao<Cron> getDao() {
		return cronDao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void updateEx(Cron cron) {
		// 数据校验
		if (!ValidateUtil.isValid(cron.getJobClass())) {
			throw new MyException("参数错误：jobClass");
		}
		if (!ValidateUtil.isValid(cron.getCron())) {
			throw new MyException("参数错误：cron");
		}

		Class<Job> jobClass = null;
		try {
			jobClass = (Class<Job>) Class.forName(cron.getJobClass());
		} catch (ClassNotFoundException e) {
			throw new MyException("无法实例化：" + cron.getJobClass());
		}
		if (!Job.class.isAssignableFrom(jobClass)) {
			throw new MyException("未实现Job接口：" + cron.getJobClass());
		}
		if (!QuartzUtil.validExpression(cron.getCron())) {
			throw new MyException("cron表达式错误：" + cron.getCron());
		}

		// 修改定时任务，重置为不启动
		cron.setUpdateUserId(getCurUser().getId());
		cron.setUpdateTime(new Date());
		cron.setState(2);
		updateById(cron);

		// 删除作业
		try {
			QuartzUtil.deleteJob(cron.getId());
		} catch (QuartzException e) {
			throw new MyException(e.getMessage());
		}
	}

	@Override
	public void delEx(Integer id) {
		// 删除作业
		try {
			QuartzUtil.deleteJob(id);
		} catch (QuartzException e) {
			throw new MyException(e.getMessage());
		}

		// 删除实体
		removeById(id);
	}

	@Override
	public List<Cron> getList() {
		return cronDao.getList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void startTask(Integer id) {
		// 启动任务
		Cron cron = getById(id);
		if (cron.getState() == 1) {
			throw new MyException("服务已启动");
		}

		Class<Job> jobClass;
		try {
			jobClass = (Class<Job>) Class.forName(cron.getJobClass());
		} catch (ClassNotFoundException e) {
			throw new MyException("实例化异常");
		}
		try {
			QuartzUtil.addJob(jobClass, cron.getId(), cron.getCron());
		} catch (QuartzException e) {
			throw new MyException(e.getMessage());
		}

		// 变更定时任务状态
		cron.setState(1);
		cron.setUpdateTime(new Date());
		cron.setUpdateUserId(getCurUser().getId());
		updateById(cron);
	}

	@Override
	public void stopTask(Integer id) {
		// 停止任务
		Cron cron = getById(id);
		if (cron.getState() == 2) {
			throw new MyException("服务已停止");
		}

		try {
			QuartzUtil.deleteJob(cron.getId());
		} catch (QuartzException e) {
			throw new MyException(e.getMessage());
		}

		// 变更定时任务状态
		cron.setState(2);
		cron.setUpdateTime(new Date());
		cron.setUpdateUserId(getCurUser().getId());
		updateById(cron);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void runOnceTask(Integer id) {
		Cron cron = getById(id);
		Class<Job> jobClass = null;
		try {
			jobClass = (Class<Job>) Class.forName(cron.getJobClass());
		} catch (ClassNotFoundException e) {
			throw new MyException("实例化异常");
		}
		try {
			jobClass.newInstance().execute(null);
		} catch (Exception e) {
			throw new MyException(e);
		}
	}
}
