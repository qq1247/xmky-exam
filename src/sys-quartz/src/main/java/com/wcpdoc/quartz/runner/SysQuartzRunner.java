package com.wcpdoc.quartz.runner;

import java.util.List;

import javax.annotation.Resource;

import org.quartz.Job;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.quartz.entity.Cron;
import com.wcpdoc.quartz.service.CronService;
import com.wcpdoc.quartz.util.QuartzUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统定时任务启动
 * 
 * v1.0 zhanghc 2019年12月16日下午11:32:55
 */
@Component
@Slf4j
public class SysQuartzRunner implements ApplicationRunner {

	@Resource
	private CronService cronService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		List<Cron> cronList = cronService.getList();
		for (Cron cron : cronList) {
			if (cron.getState() != 1) {
				log.info("系统定时任务启动：【{}】未启动", cron.getName());
				continue;
			}

			try {
				log.info("系统定时任务启动：【{}】启动", cron.getName());
				@SuppressWarnings("unchecked")
				Class<Job> jobClass = (Class<Job>) Class.forName(cron.getJobClass());
				QuartzUtil.addJob(jobClass, cron.getId(), cron.getCron());
			} catch (Exception e) {
				log.error(String.format("系统定时任务启动：【{}】启动失败：", cron.getName()), e);
			}
		}
	}
}
