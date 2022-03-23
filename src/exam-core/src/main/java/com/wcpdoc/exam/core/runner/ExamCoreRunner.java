package com.wcpdoc.exam.core.runner;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;

/**
 * 考试核心启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class ExamCoreRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(ExamCoreRunner.class);
	
	@Resource
	private ExamService examService;
	@Resource
	private MyExamDetailService myExamDetailService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		new Thread(new Runnable() {// SpringApplication.callRunners方法会顺序执行ApplicationRunner实现，while (true)不返回导致其他任务不执行
			@Override
			public void run() {
				// 服务启动的时候，加载未阅卷的考试列表
				AutoMarkCache.reloadCache();
				List<Exam> unMarkExamList = AutoMarkCache.getList();
				for (Exam exam : unMarkExamList) {
					log.info("考试核心启动：【{}-{}】加入监听，{}开始自动阅卷", 
							exam.getId(), 
							exam.getName(), // 未阅卷 取 考试结束时间；阅卷中 取 阅卷结束时间
							exam.getMarkState() == 1 ? DateUtil.formatDateTime(exam.getEndTime()) : DateUtil.formatDateTime(exam.getMarkEndTime()));
				}
				
				// 监听阅卷时间到，自动阅卷
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					unMarkExamList = AutoMarkCache.getList(); // 每次重新取，因为运行中会动态添加删除等。如新发布的考试
					for (Exam unMarkExam : unMarkExamList) {
						try {
							if ((unMarkExam.getMarkState() == 1 && unMarkExam.getEndTime().getTime() > System.currentTimeMillis())
									|| (unMarkExam.getMarkState() == 2 && unMarkExam.getMarkEndTime().getTime() > System.currentTimeMillis())) {
								continue;
							}
							
							AutoMarkCache.del(unMarkExam.getId());// 删除缓存不在监听，如果阅卷报错，在执行一遍也报错。应该给管理员发送邮件，修复问题后，刷新缓存重新执行任务
							if (unMarkExam.getMarkState() == 1) {
								myExamDetailService.doExam(unMarkExam.getId());
							} else if (unMarkExam.getMarkState() == 2){
								myExamDetailService.doMark(unMarkExam.getId());
							}
						} catch (MyException e) {// 一个有问题，不影响其他任务执行
							log.error("自动阅卷错误：{}", e.getMessage());
						} catch (Exception e) {
							log.error("自动阅卷错误：", e);
						}
					}
				}
			}
		}).start();
	}
}
