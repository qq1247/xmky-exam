package com.wcpdoc.exam.core.service;

import com.wcpdoc.exam.core.entity.ExamType;

/**
 * 考试分类扩展服务层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeExService{

	/**
	 * 删除关联引用
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param examType 
	 * void
	 */
	void delAndUpdate(ExamType examType);
	
}

