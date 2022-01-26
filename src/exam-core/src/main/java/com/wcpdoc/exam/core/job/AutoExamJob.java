package com.wcpdoc.exam.core.job;

import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoExamCache;
import com.wcpdoc.exam.core.service.MyExamDetailService;

/**
 * 自动考试任务 （考试结束时间到，自动阅卷）
 * 该任务每间隔1秒执行一次
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public class AutoExamJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(AutoExamJob.class);
	private static final MyExamDetailService myExamDetailService = SpringUtil.getBean(MyExamDetailService.class);
	private static boolean TASK_RUN = false;// 上一次任务是否正在运行中

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 校验数据有效性
		try {
			if (TASK_RUN) {
				log.info("自动考试：上一次任务正在运行中，本次不做任何操作");
				return;
			}
			TASK_RUN = true;
			
			List<Integer> examList = AutoExamCache.getList();
			if (!ValidateUtil.isValid(examList)) {
				return;
			}
			
			for (Integer examId : examList) {
				try {
					if (AutoExamCache.get(examId).getTime() > System.currentTimeMillis()) {// 如果考试未结束，继续等待
						continue;
					}
					
					// 完成考试
					AutoExamCache.del(examId);// 先清理掉缓存，定时任务不在监控（如果完成考试报错，在执行一遍也报错，通过页面添加按钮，人工修复后手动执行）
					myExamDetailService.doExam(examId);
				} catch (MyException e) {// 一个有问题，不影响其他任务执行
					log.error("自动考试错误：{}", e.getMessage());
				} catch (Exception e) {
					log.error("自动考试错误：", e);
				}
			}
		} catch (Exception e) {
			log.error("自动考试错误：", e);
		} finally {
			TASK_RUN = false;
		}
	}
}
