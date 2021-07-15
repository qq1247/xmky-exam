package com.wcpdoc.exam.core.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamTypeExService;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
		if (ValidateUtil.isValid(examList)) {
			throw new MyException("该考试分类下有试题，不允许删除！");
		}
	}
}
