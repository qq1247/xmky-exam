package com.wcpdoc.exam.report.service;

import java.util.Map;

/**
 * 统计服务层接口
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
public interface GradeService {

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
	Map<String, Object> slowLog();
	
	/**
	 * 分数统计
	 * 
	 * v1.0 chenyun 2021年3月24日上午10:04:05
	 * @param examId
	 * @return Map<String,Object>
	 */
	Map<String, Object> count(Integer examId);
}
