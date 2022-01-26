package com.wcpdoc.exam.core.runner;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.exam.core.cache.AutoExamCache;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.service.ExamService;

/**
 * 考试服务启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class ExamRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(ExamRunner.class);
	
	@Resource
	private ExamService examService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 查找未阅卷的考试，加入定时任务监听，用于考试结束时自动阅卷
		PageIn pageIn = new PageIn();
		pageIn.setPageSize(100);
		pageIn.addAttr("state", "1");// 已发布
		pageIn.addAttr("markState", "1");// 未阅卷
		List<Map<String, Object>> resultList = examService.getListpage(pageIn).getList();
		for (Map<String, Object> result : resultList) {
			AutoExamCache.put((Integer)result.get("id"), DateUtil.getDateTime(result.get("endTime").toString()));
			log.info("启动监听：【{}-{}】加入监听，{}开始自动阅卷", result.get("id"), result.get("name"), result.get("endTime"));
		}
		
		// 查找需要完成阅卷的考试，加入定时任务监听，用于阅卷结束时自动阅卷
		pageIn = new PageIn();
		pageIn.setPageSize(100);
		pageIn.addAttr("state", "1");// 已发布
		pageIn.addAttr("markState", "2"); // 阅卷中
		resultList = examService.getListpage(pageIn).getList();
		for (Map<String, Object> result : resultList) {
			AutoMarkCache.put((Integer)result.get("id"), DateUtil.getDateTime(result.get("markEndTime").toString()));
			log.info("启动监听：【{}-{}】加入监听，{}开始完成阅卷", result.get("id"), result.get("name"), result.get("markEndTime"));
		}
	}
}
