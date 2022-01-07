package com.wcpdoc.exam.report.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 统计服务层接口
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
public interface ReportService {

	/**
	 * 首页用户
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> homeUser();
	
	/**
	 * 首页子管理
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> homeSubAdmin();
	
	/**
	 * 首页管理
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> homeAdmin();
	
	/**
	 * 慢接口日志
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	List<String> serverLog() throws Exception ;
	
	/**
	 * 试题统计
	 * 
	 * v1.0 chenyun 2021年12月15日下午1:47:12
	 * @param questionTypeId
	 * @return Map<String,Object>
	 */
	Map<String, Object> questionStatis(Integer questionTypeId);
	
	/**
	 * 考试统计
	 * 
	 * v1.0 chenyun 2021年12月15日下午1:47:12
	 * @param examId
	 * @return Map<String,Object>
	 */
	Map<String, Object> examStatis(Integer examId);
	
	/**
	 * 考试排名
	 * 
	 * v1.0 chenyun 2021年12月15日下午1:47:12
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut myExamListpage(PageIn pageIn);
	
	/**
	 * 错题分析
	 * 
	 * v1.0 chenyun 2021年12月16日下午1:45:11
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut questionListpage(PageIn pageIn);
	
	/**
	 * 分数统计
	 * 
	 * v1.0 chenyun 2021年3月24日上午10:04:05
	 * @param examId
	 * @return Map<String,Object>
	 */
	Map<String, Object> count(Integer examId);
}
