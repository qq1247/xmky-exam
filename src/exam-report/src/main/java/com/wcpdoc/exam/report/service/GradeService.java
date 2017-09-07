package com.wcpdoc.exam.report.service;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 成绩服务层接口
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
public interface GradeService {

	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2017年8月29日下午2:03:36
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getListpage(PageIn pageIn);
}
