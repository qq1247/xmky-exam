package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyMark;
/**
 * 我的阅卷服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkService extends BaseService<MyMark>{

	/**
	 * 获取我的阅卷列表
	 * 
	 * v1.0 zhanghc 2023年1月30日下午6:32:13
	 * @param examId 
	 * @return List<MyMark>
	 */
	List<MyMark> getList(Integer examId);
	
	/**
	 * 考试用户列表
	 * 
	 * v1.0 zhanghc 2022年11月9日下午3:01:00
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);

	/**
	 * 分配试卷
	 * 
	 * v1.0 zhanghc 2023年2月23日下午2:33:26
	 * @param examId
	 * @param num void
	 */
	void assign(Integer examId, Integer num);

	/**
	 * 打分
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId 考试ID
	 * @param userId 考试用户ID
	 * @param questionId 试题ID
	 * @param userScore 用户得分
	 * @param finish 是否全部阅完。是：会合计总分
	 * void
	 */
	void scoreUpdate(Integer examId, Integer userId, Integer questionId, BigDecimal userScore);

	/**
	 * 阅卷
	 * 
	 * v1.0 chenyun 2021年8月24日上午9:46:01
	 * @param examId
	 * @param userId
	 * void
	 */
	void finish(Integer examId, Integer userId);
	
}
