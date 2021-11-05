package com.wcpdoc.exam.report.service;

import java.util.Map;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

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
	
	/**
	 * 分数统计
	 * 
	 * v1.0 chenyun 2021年3月24日上午10:04:05
	 * @param examId
	 * @return Map<String,Object>
	 */
	Map<String, Object> count(Integer examId);
}
