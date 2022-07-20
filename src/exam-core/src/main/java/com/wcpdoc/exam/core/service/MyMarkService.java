package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyMark;
/**
 * 我的阅卷服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkService extends BaseService<MyMark>{

	/**
	 * 获取获取阅卷列表
	 * 
	 * v1.0 zhanghc 2020年9月29日下午5:09:50
	 * @param examId
	 * @return List<MyExam>
	 */
	List<MyMark> getList(Integer examId);
	
	/**
	 * 获取阅卷列表
	 * 
	 * v1.0 zhanghc 2022年7月18日下午3:26:40
	 * @param markUserId
	 * @return List<MyMark>
	 */
	List<MyMark> getListForMarkUser(Integer markUserId);
	
	/**
	 * 阅卷
	 * 
	 * v1.0 chenyun 2021年8月24日上午9:41:53
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @param score
	 * void
	 */
	void scoreUpdate(Integer examId, Integer userId, Integer questionId, BigDecimal score);
	
	/**
	 * 完成阅卷
	 * 
	 * v1.0 chenyun 2021年8月24日上午9:46:01
	 * @param examId
	 * @param userId
	 * void
	 */
	void finish(Integer examId, Integer userId);
	
}
