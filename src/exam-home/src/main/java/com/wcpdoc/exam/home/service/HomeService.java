package com.wcpdoc.exam.home.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.User;
/**
 * 首页服务层接口
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
public interface HomeService extends BaseService<Object>{

	/**
	 * 获取权限总和
	 * 
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @param userId
	 * @return Map<Integer,Long>
	 */
	Map<Integer, Long> getAuthSum(Integer userId);

	/**
	 * 获取未结束的考场列表
	 * 
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @param userId
	 * @return List<Exam>
	 */
	List<Map<String, Object>> getUnFinishExamList(Integer userId);

	/**
	 * 完成登录
	 * 
	 * v1.0 zhanghc 2017年8月3日上午10:55:36
	 * @param loginName
	 * @param pwd
	 * @param request
	 * @return User
	 */
	User doIn(String loginName, String pwd, HttpServletRequest request);

	/**
	 * 完成退出
	 * 
	 * v1.0 zhanghc 2017年8月3日上午11:20:04
	 * @param request
	 * void
	 */
	void doOut(HttpServletRequest request);

}
