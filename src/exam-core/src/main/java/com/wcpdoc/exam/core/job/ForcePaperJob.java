package com.wcpdoc.exam.core.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.util.SpringUtil;
import com.wcpdoc.exam.file.job.ClearFileJob;

/**
 * 强制交卷任务
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public class ForcePaperJob implements Job{
	private static final Logger log = LoggerFactory.getLogger(ClearFileJob.class);
	private static final ExamService examService = SpringUtil.getBean(ExamService.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		//标记为强制交卷
		log.info("开始强制交卷");
		
		User user = new User();
		user.setId(1);
		user.setName("系统管理员");
		user.setLoginName("sysadmin");
		examService.forcePaper(user);
		log.info("成功强制交卷");
	}

}
