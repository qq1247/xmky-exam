package com.wcpdoc.exam.report.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;

/**
 * 统计数据访问层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface ReportDao extends RBaseDao<Object> {
	
	/**
	 * 管理员首页
	 * 
	 * v1.0 chenyun 2021年12月10日上午10:14:11
	 * @return Map<String,Object>
	 */
	Map<String, Object> adminHome();
	
	/**
	 * 用户首页
	 * 
	 * v1.0 zhanghc 2023年3月19日下午2:06:57
	 * @param userId
	 * @return Map<String,Object>
	 */
	Map<String, Object> userHome(Integer userId);
	
	/**
	 * 子管理员首页
	 * 
	 * v1.0 zhanghc 2023年3月19日下午2:06:57
	 * @param userId 子管理员ID
	 * @return Map<String,Object>
	 */
	Map<String, Object> subAdminHome(Integer userId);
	
	/**
	 * 阅卷用户首页
	 * 
	 * v1.0 zhanghc 2023年9月26日下午2:09:05
	 * @param userId 阅卷用户ID
	 * @return Map<String,Object>
	 */
	Map<String, Object> markUserHome(Integer user);
	
	/**
	 * 我的考试列表
	 * 
	 * v1.0 chenyun 2021年12月15日下午1:47:12
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut examRankListpage(PageIn pageIn);

	/**
	 * 试卷列表
	 * 
	 * v1.0 chenyun 2021年12月16日上午9:38:30
	 * @param examId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> questionErrList(Integer examId);
	
	/**
	 * 分数统计
	 * 
	 * v1.0 chenyun 2021年3月24日上午10:04:05
	 * @param examId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> count(Integer examId);

}
