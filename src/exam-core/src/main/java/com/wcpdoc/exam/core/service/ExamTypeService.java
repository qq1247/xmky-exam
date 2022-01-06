package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExamType;
/**
 * 考试分类服务层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeService extends BaseService<ExamType> {
	
	/**
	 * 添加考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param examType
	 * void
	 */
	void addAndUpdate(ExamType examType);
	
	/**
	 * 修改考试分类
	 * 
	 * v1.0 cy 2021年6月11日下午2:44:38
	 * @param examType void
	 */
	void editAndUpdate(ExamType examType);
	
	/**
	 * 删除考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
}
