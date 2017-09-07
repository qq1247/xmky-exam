package com.wcpdoc.exam.report.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.report.service.GradeService;

/**
 * 日志服务层实现
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Service
public class GradeServiceImpl implements GradeService {
	@Resource
	private ExamService examService;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return examService.getGradeListpage(pageIn);
	}
}
