package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.service.BaseService;
/**
 * 我的阅卷服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkService extends BaseService<Object>{
	/**
	 * 阅题
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId 考试ID
	 * @param userId 考试用户ID
	 * @param questionId 试题ID
	 * @param userScore 用户得分
	 * @param finish 是否全部阅完。是：会合计总分
	 * void
	 */
	void scoreUpdate(Integer examId, Integer userId, Integer questionId, BigDecimal userScore, Boolean finish);

	/**
	 * 考试用户列表
	 * 
	 * v1.0 zhanghc 2022年11月9日下午3:01:00
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);
	
}
