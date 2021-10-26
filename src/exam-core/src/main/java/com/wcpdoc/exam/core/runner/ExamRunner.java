package com.wcpdoc.exam.core.runner;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.util.DateUtil;

/**
 * 考试服务启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class ExamRunner implements ApplicationRunner {
	@Resource
	private ExamService examService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 服务启动的时候，查找所有考试未结束，并且未自动阅卷的考试，加入定时任务监听，用于考试结束时自动阅卷
		PageIn pageIn = new PageIn();
		pageIn.setPageSize(100);
		pageIn.addAttr("endTime1", DateUtil.formatDateTime(new Date()));
		pageIn.addAttr("state", "1");
		pageIn.addAttr("markState", "2");
		List<Map<String, Object>> resultList = examService.getListpage(pageIn).getList();
		for (Map<String, Object> result : resultList) {
			AutoMarkCache.put((Integer)result.get("id"), DateUtil.getDateTime(result.get("endTime").toString()));
		}
	}
}
