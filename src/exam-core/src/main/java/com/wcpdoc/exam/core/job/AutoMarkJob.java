package com.wcpdoc.exam.core.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.service.MyExamDetailService;

/**
 * 自动阅卷任务 
 * 该任务每间隔1秒执行一次
 * 
 * v1.0 chenyun 2021-11-30 14:31:15
 */
public class AutoMarkJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(AutoMarkJob.class);
	private static final MyExamDetailService myExamDetailService = SpringUtil.getBean(MyExamDetailService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		List<Integer> examList = AutoMarkCache.getList();
		if (examList == null || examList.size() == 0) {
			return;
		}
		for (Integer examId : examList) {
			try {
				if (AutoMarkCache.get(examId).getTime() > System.currentTimeMillis()) {// 如果阅卷未结束，不处理
					continue;
				}

				AutoMarkCache.del(examId);// 考试时间已结束，先清理监听在完成阅卷。可能造成的问题是正在阅卷，定时任务又执行一次。
				myExamDetailService.outMark(examId);// 完成阅卷
			} catch (MyException e) {// 一个有问题，不要影响其他任务执行。
				log.error("自动阅卷错误：{}", e.getMessage());
				continue;
			} catch (Exception e) {
				log.error("自动阅卷错误：", e);
				continue;
			}
		}
	}
}
