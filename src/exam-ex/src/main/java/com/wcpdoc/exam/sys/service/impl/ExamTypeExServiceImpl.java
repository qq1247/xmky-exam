package com.wcpdoc.exam.sys.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.exam.entity.Exam;
import com.wcpdoc.exam.exam.entity.ExamType;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.exam.service.ExamTypeExService;

/**
 * 考试分类扩展服务层实现
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Service
public class ExamTypeExServiceImpl implements ExamTypeExService {

	@Resource
	private ExamService examService;

	@Override
	public void delAndUpdate(ExamType examType) {
		List<Exam> examList = examService.getList(examType.getId());
		for(Exam exam : examList){
			exam.setExamTypeId(1);
		}
	}
}
