package com.wcpdoc.exam.core.runner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyPaperService;

import lombok.extern.slf4j.Slf4j;

/**
 * 考试核心启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
@Slf4j
public class ExamCoreRunner implements ApplicationRunner {

	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private BaseCacheService baseCacheService;
	@Resource
	private MyPaperService myPaperService;

	private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(CPU_NUM);

	@Override // SpringApplication.callRunners方法会顺序执行ApplicationRunner实现，while(true)不返回导致其他任务不执行
	public void run(ApplicationArguments args) throws Exception {

		// 查找待阅试卷，自动批阅
		new Thread(() -> {
			while (true) {
				try {
					TimeUnit.SECONDS.sleep(1);

					List<Callable<Boolean>> taskList = examCacheService.getUnMarkList().stream()//
							.map(myExam -> new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									Exam exam = examCacheService.getExam(myExam.getExamId());
									User examUser = baseCacheService.getUser(myExam.getUserId());
									try {
										MyExam result = myPaperService.doMark(myExam.getExamId(), myExam.getUserId());
										log.info("自动批阅【{}-{}】下【{}-{}】的客观题部分，得{}分", exam.getId(), exam.getName(),
												examUser.getLoginName(), examUser.getName(),
												result.getObjectiveScore());
									} catch (MyException e) {
										log.error(String.format("自动批阅【%s-%s】下【%s-%s】的客观题部分，错误：%s", exam.getId(),
												exam.getName(), examUser.getLoginName(), examUser.getName(),
												e.getMessage()));
									} catch (Exception e) {
										log.error(
												String.format("自动批阅【%s-%s】下【%s-%s】的客观题部分，错误：", exam.getId(),
														exam.getName(), examUser.getLoginName(), examUser.getName()),
												e);
									}

									return null;
								}
							}).collect(Collectors.toList());

					EXECUTOR_SERVICE.invokeAll(taskList);
				} catch (Exception e) {
					log.error(String.format("自动批阅未知错误，30秒后重新执行："), e);
					try {
						TimeUnit.SECONDS.sleep(30);
					} catch (InterruptedException e1) {
					}
				}
			}
		}).start();

		// 查找已结束的考试，进行收尾
		new Thread(() -> {
			while (true) {
				try {
					TimeUnit.SECONDS.sleep(1);

					Long curTime = System.currentTimeMillis();
					List<Callable<Boolean>> taskList = examCacheService.getExamingList().stream()//
							.filter(exam -> (exam.getMarkState() == 1 && exam.getEndTime().getTime() <= curTime)
									|| (exam.getMarkState() == 2 && exam.getMarkEndTime().getTime() <= curTime))//
							.map(exam -> new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									try {
										log.info("自动结束【{}-{}】的考试", exam.getId(), exam.getName());
										myPaperService.doExam(exam.getId());
									} catch (MyException e) {
										log.error(String.format("自动结束【%s-%s】的考试错误：%s", exam.getId(), exam.getName(),
												e.getMessage()));
									} catch (Exception e) {
										log.error(String.format("自动结束【%s-%s】的考试错误：", exam.getId(), exam.getName()), e);
									}
									return null;
								}
							}).collect(Collectors.toList());

					EXECUTOR_SERVICE.invokeAll(taskList);
				} catch (Exception e) {
					log.error(String.format("自动结束考试未知错误，30秒后重新执行："), e);
					try {
						TimeUnit.SECONDS.sleep(30);
					} catch (InterruptedException e1) {
					}
				}
			}
		}).start();

		// 查找即将开始的考试，提前缓存数据，增强整体考试性能
		new Thread(() -> {
			Set<Integer> examIds = new HashSet<>();
			while (true) {
				try {
					TimeUnit.SECONDS.sleep(1);

					Long curTime = System.currentTimeMillis();
					List<Callable<Boolean>> taskList = examCacheService.getExamingList().stream()//
							.filter(exam -> !examIds.contains(exam.getId())// 已缓存则不在执行
									&& (exam.getStartTime().getTime() - curTime <= 30 * 1000
											&& exam.getStartTime().getTime() - curTime >= 6 * 1000)) // 开考前30秒-开考前6秒，中间的时间用来缓存数据
							.map(exam -> new Callable<Boolean>() {
								@Override
								public Boolean call() throws Exception {
									try {
										examIds.add(exam.getId());
										examCacheService.getExam(exam.getId());
										List<MyExam> myExamList = examCacheService.getMyExamList(exam.getId());
										for (MyExam myExam : myExamList) {
											User examUser = baseCacheService.getUser(myExam.getUserId());
											baseCacheService.getOrg(examUser.getOrgId());
											examCacheService.getMyExam(myExam.getExamId(), myExam.getUserId());
											myPaperService.generatePaper(myExam.getExamId(), myExam.getUserId(), false,
													false);
											log.info("自动缓存【{}-{}】下【{}-{}】的试卷", exam.getId(), exam.getName(),
													examUser.getLoginName(), examUser.getName());
										}
									} catch (MyException e) {
										log.error(String.format("自动缓存【%s-%s】的试卷错误：%s", exam.getId(), exam.getName(),
												e.getMessage()));
									} catch (Exception e) {
										log.error(String.format("自动缓存【%s-%s】的试卷错误：", exam.getId(), exam.getName()), e);
									}
									return null;
								}
							}).collect(Collectors.toList());

					EXECUTOR_SERVICE.invokeAll(taskList);
				} catch (Exception e) {
					log.error(String.format("自动缓存试卷未知错误，30秒后重新执行："), e);
					try {
						TimeUnit.SECONDS.sleep(30);
					} catch (InterruptedException e1) {
					}
				}
			}
		}).start();
	}
}
