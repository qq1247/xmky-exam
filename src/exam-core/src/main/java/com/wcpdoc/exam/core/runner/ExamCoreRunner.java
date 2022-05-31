package com.wcpdoc.exam.core.runner;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.notify.exception.NotifyException;
import com.wcpdoc.notify.service.NotifyService;

/**
 * 考试核心启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class ExamCoreRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(ExamCoreRunner.class);
	
	@Resource
	private MyExamService myExamService;
	@Resource
	private NotifyService notifyService;

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
						log.error("自动阅卷错误：", e1);
					}
					
					unMarkExamList = AutoMarkCache.getList(); // 每次重新取，因为运行中会动态添加删除等。如新发布的考试
					for (Exam unMarkExam : unMarkExamList) {
						if ((unMarkExam.getMarkState() == 1 && unMarkExam.getEndTime().getTime() > System.currentTimeMillis())
								|| (unMarkExam.getMarkState() == 2 && unMarkExam.getMarkEndTime().getTime() > System.currentTimeMillis())) {
							continue;
						}
						
						try {
							if (!AutoMarkCache.tryWriteLock(unMarkExam.getId(), 10000)) {// 尝试加写锁，答题，交卷，变更时间等功能暂停
								log.info("自动阅卷错误：【{}-{}】尝试加写锁失败，耗时10秒，等待下一次运行", unMarkExam.getId(), unMarkExam.getName());
								continue;
							}
						} catch (Exception e) {
							log.error("自动阅卷错误：【{}-{}】尝试加写锁失败，强制被中断，等待下一次运行，{}", unMarkExam.getId(), unMarkExam.getName(), e.getMessage());
							continue;
						}
						
						unMarkExam = AutoMarkCache.get(unMarkExam.getId());
						if ((unMarkExam == null 
								|| unMarkExam.getMarkState() == 1 && unMarkExam.getEndTime().getTime() > System.currentTimeMillis())
								|| (unMarkExam.getMarkState() == 2 && unMarkExam.getMarkEndTime().getTime() > System.currentTimeMillis())) {
							log.info("自动阅卷错误：【{}-{}】第二次读取缓存时数据变更，等待下一次运行", unMarkExam.getId(), unMarkExam.getName());
							continue;// 二次判断，因为有可能时间已经变更
						}
						
						try {
							if (unMarkExam.getMarkState() == 1) {
								myExamService.doExam(unMarkExam.getId());
							} else if (unMarkExam.getMarkState() == 2){
								myExamService.doMark(unMarkExam.getId());
							}
						} catch (MyException e) {// 一个有问题，不影响其他任务执行
							log.error("自动阅卷错误：{}", e.getMessage());
							AutoMarkCache.del(unMarkExam.getId());// 如果已经异常，给管理员发送邮件
							sendEmail(e);
						} catch (Exception e) {
							log.error("自动阅卷错误：", e);
							AutoMarkCache.del(unMarkExam.getId());
							sendEmail(e);
						} finally {
							AutoMarkCache.releaseWriteLock(unMarkExam.getId());// 释放写锁
						}
					}
				}
			}

			private void sendEmail(Exception e) {
				Parm parm = ParmCache.get();
				try {
					if (!ValidateUtil.isValid(parm.getEmailUserName())) {
						log.info("自动阅卷错误：没有配置管理员邮箱，不能推送异常信息");
						return;
					}
					
					notifyService.pushEmail(parm.getEmailUserName(), parm.getEmailUserName(), "在线考试-自动阅卷失败", e.getMessage());
				} catch (NotifyException e1) {
					log.error("自动阅卷错误：{}", e.getMessage());
				}
			}
		}).start();
	}
}

