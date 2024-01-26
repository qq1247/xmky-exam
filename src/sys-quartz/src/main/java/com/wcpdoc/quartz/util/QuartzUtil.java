package com.wcpdoc.quartz.util;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.spi.OperableTrigger;

import com.wcpdoc.quartz.exception.QuartzException;

import lombok.extern.slf4j.Slf4j;

/**
 * 定时任务工具类
 * 
 * v1.0 zhanghc 2019年7月5日上午10:31:15
 */
@Slf4j
public class QuartzUtil {
	/** 作业名称前缀 */
	private final static String JOB_NAME_PRE = "TASK_";
	private static Scheduler scheduler;

	static {
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			log.error("获取定时器异常：", e);
			try {
				throw new QuartzException("获取定时器异常");
			} catch (QuartzException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * 添加作业
	 * 
	 * v1.0 zhanghc 2019年7月5日上午11:47:30
	 * 
	 * @param jobClass
	 * @param jobId
	 * @param cronExp  void
	 * @throws QuartzException
	 */
	public static void addJob(Class<? extends Job> jobClass, Integer jobId, String cronExp) throws QuartzException {
		try {
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId)).build();

			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId))
					.withSchedule(CronScheduleBuilder.cronSchedule(cronExp).withMisfireHandlingInstructionDoNothing())
					.build();

			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			log.error("添加作业异常：", e);
			throw new QuartzException("添加作业异常");
		} catch (Exception e) {
			log.error("未知异常：", e);
			throw new QuartzException("未知异常");
		}
	}

	/**
	 * 更新作业
	 * 
	 * v1.0 zhanghc 2019年7月5日上午11:48:32
	 * 
	 * @param jobId
	 * @param cronExp void
	 * @throws QuartzException
	 */
	public static void updateJob(Class<? extends Job> jobClass, Integer jobId, String cronExp) throws QuartzException {
		deleteJob(jobId);
		addJob(jobClass, jobId, cronExp);
	}

	/**
	 * 执行一次作业
	 * 
	 * v1.0 zhanghc 2019年7月5日上午11:50:26
	 * 
	 * @param jobId void
	 * @throws QuartzException
	 */
	public static void executeOnceJob(Integer jobId) throws QuartzException {
		try {
			scheduler.triggerJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			log.error("执行一次作业异常：", e);
			throw new QuartzException("执行一次作业异常");
		} catch (Exception e) {
			log.error("未知异常：", e);
			throw new QuartzException("未知异常");
		}
	}

	/**
	 * 暂停作业
	 * 
	 * v1.0 zhanghc 2019年7月5日上午11:50:26
	 * 
	 * @param jobId void
	 * @throws QuartzException
	 */
	public static void pauseJob(Integer jobId) throws QuartzException {
		try {
			scheduler.pauseJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			log.error("暂停作业异常：", e);
			throw new QuartzException("暂停作业异常");
		} catch (Exception e) {
			log.error("未知异常：", e);
			throw new QuartzException("未知异常");
		}
	}

	/**
	 * 恢复作业
	 * 
	 * v1.0 zhanghc 2019年7月5日上午11:50:26
	 * 
	 * @param jobId void
	 * @throws QuartzException
	 */
	public static void resumeJob(Integer jobId) throws QuartzException {
		try {
			scheduler.resumeJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			log.error("恢复作业异常：", e);
			throw new QuartzException("恢复作业异常");
		} catch (Exception e) {
			log.error("未知异常：", e);
			throw new QuartzException("未知异常");
		}
	}

	/**
	 * 删除作业
	 * 
	 * v1.0 zhanghc 2019年7月5日上午11:50:26
	 * 
	 * @param jobId void
	 * @throws QuartzException
	 */
	public static void deleteJob(Integer jobId) throws QuartzException {
		try {
			scheduler.deleteJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			log.error("删除作业异常：", e);
			throw new QuartzException("删除作业异常");
		} catch (Exception e) {
			log.error("未知异常：", e);
			throw new QuartzException("未知异常");
		}
	}

	/**
	 * 获取最近几次触发时间
	 * 
	 * v1.0 zhanghc 2019年7月6日上午9:30:52
	 * 
	 * @param trigger
	 * @param num
	 * @return List<Date>
	 */
	public static List<Date> getRecentTriggerTime(OperableTrigger trigger, int num) {
		return TriggerUtils.computeFireTimes(trigger, null, num);
	}

	/**
	 * 获取最近几次触发时间
	 * 
	 * v1.0 zhanghc 2019年7月6日上午9:33:24
	 * 
	 * @param cronExp
	 * @param num
	 * @return List<Date>
	 * @throws QuartzException
	 */
	public static List<Date> getRecentTriggerTime(String cronExp, int num) throws QuartzException {
		CronTriggerImpl cronTrigger = new CronTriggerImpl();
		try {
			cronTrigger.setCronExpression(cronExp);
		} catch (ParseException e) {
			log.error("表达式错误：", e);
			throw new QuartzException("表达式错误");
		}

		return getRecentTriggerTime(cronTrigger, num);
	}

	/**
	 * 获取触发器
	 * 
	 * v1.0 zhanghc 2019年7月5日上午10:43:23
	 * 
	 * @param jobId
	 * @return Trigger
	 * @throws QuartzException
	 */
	public static Trigger getTrigger(Integer jobId) throws QuartzException {
		try {
			return scheduler.getTrigger(getTriggerKey(jobId));
		} catch (SchedulerException e) {
			log.error("获取触发器异常：", e);
			throw new QuartzException("获取触发器异常");
		} catch (Exception e) {
			log.error("未知异常：", e);
			throw new QuartzException("未知异常");
		}
	}

	/**
	 * 获取触发器key
	 * 
	 * v1.0 zhanghc 2019年7月5日上午10:36:05
	 * 
	 * @param jobId
	 * @return TriggerKey
	 */
	public static TriggerKey getTriggerKey(Integer jobId) {
		return TriggerKey.triggerKey(JOB_NAME_PRE + jobId);
	}

	/**
	 * 获取作业key
	 * 
	 * v1.0 zhanghc 2019年7月5日上午10:36:39
	 * 
	 * @param jobId
	 * @return JobKey
	 */
	public static JobKey getJobKey(Integer jobId) {
		return JobKey.jobKey(JOB_NAME_PRE + jobId);
	}

	public static Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * 重新设置定时器<br/>
	 * 会关闭旧定时器<br/>
	 * 
	 * v1.0 zhanghc 2019年7月6日上午10:01:52
	 * 
	 * @param scheduler void
	 * @throws QuartzException
	 */
	public static void setScheduler(Scheduler scheduler) throws QuartzException {
		if (scheduler.equals(QuartzUtil.scheduler)) {
			return;
		}

		try {
			if (!QuartzUtil.scheduler.isShutdown()) {
				QuartzUtil.scheduler.shutdown();
			}
		} catch (SchedulerException e) {
			log.error("关闭定时器异常：", e);
			throw new QuartzException("关闭定时器异常");
		}

		try {
			if (!scheduler.isStarted()) {
				scheduler.start();
			}
		} catch (SchedulerException e) {
			log.error("启动定时器异常：", e);
			throw new QuartzException("启动定时器异常");
		}

		QuartzUtil.scheduler = scheduler;
	}

	/**
	 * 校验cron表达式
	 * 
	 * v1.0 zhanghc 2019年9月13日下午6:30:30
	 * 
	 * @param cron
	 * @return boolean
	 */
	public static boolean validExpression(String cron) {
		return CronExpression.isValidExpression(cron);
	}
}
