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
	 * 用户首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> userHome();
	
	/**
	 * 管理员首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> adminHome();
	
	/**
	 * 子管理员首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> subAdminHome();
	
	/**
	 * 阅卷用户首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> markUserHome();
	
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
	 * @return Map<String, Object>
	 */
	Map<String, Object> questionStatis(Integer questionTypeId);
	
	/**
	 * 考试统计
	 * 
	 * v1.0 chenyun 2021年12月15日下午1:47:12
	 * @param examId
	 * @return Map<String, Object>
	 */
	Map<String, Object> examStatis(Integer examId);
	
	/**
	 * 考试排名
	 * 
	 * v1.0 chenyun 2021年12月15日下午1:47:12
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut examRankListpage(PageIn pageIn);
	
//	/**
//	 * 错题分析
//	 * 
//	 * v1.0 chenyun 2021年12月16日下午1:45:11
//	 * @param examId
//	 * @return List<Map<String, Object>>
//	 */
//	List<Map<String, Object>> questionErrList(Integer examId);
	

}
