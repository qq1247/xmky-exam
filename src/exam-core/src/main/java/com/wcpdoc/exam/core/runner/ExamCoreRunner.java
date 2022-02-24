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
 * 考试核心初始化
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
		// 加载未阅卷的考试列表
		AutoMarkCache.reloadCache();
		List<Exam> examList = AutoMarkCache.getList();
		for (Exam exam : examList) {
			log.info("启动监听：【{}-{}】加入监听，{}开始自动阅卷", 
					exam.getId(), 
					exam.getName(), // 未阅卷 取 考试结束时间；阅卷中 取 阅卷结束时间
					exam.getMarkState() == 1 ? DateUtil.formatDateTime(exam.getEndTime()) : DateUtil.formatDateTime(exam.getMarkEndTime()));
		}
		
		// 监听阅卷时间到，自动阅卷
		while (true) {
			TimeUnit.SECONDS.sleep(1);
			
			examList = AutoMarkCache.getList(); // 每次重新取，因为运行中会动态添加删除等。如新发布的考试
			for (Exam exam : examList) {
				try {
					if (exam.getMarkState() == 1 && exam.getEndTime().getTime() <= System.currentTimeMillis()) {
						AutoMarkCache.del(exam.getId());// 先清理掉缓存，不在监听（如果阅卷报错，在执行一遍也报错，应该人工修复问题后，管理员页面，手动点击刷新缓存重新执行任务）
						myExamDetailService.doExam(exam.getId());
					} else if (exam.getMarkState() == 2 && exam.getMarkEndTime().getTime() <= System.currentTimeMillis()) {
						AutoMarkCache.del(exam.getId());
						myExamDetailService.doMark(exam.getId());
					}
				} catch (MyException e) {// 一个有问题，不影响其他任务执行
					//TODO 发送错误信息给管理员
					AutoMarkCache.del(exam.getId());
					log.error("自动阅卷错误：{}", e.getMessage());
				} catch (Exception e) {
					//TODO 发送错误信息给管理员
					AutoMarkCache.del(exam.getId());
					log.error("自动阅卷错误：", e);
				}
			}
		}
	}
}
