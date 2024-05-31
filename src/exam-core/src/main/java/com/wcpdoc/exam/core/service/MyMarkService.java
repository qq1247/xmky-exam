package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.ex.PaperPart;

/**
 * 我的阅卷服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyMarkService extends BaseService<MyMark> {
	/**
	 * 考试用户列表
	 * 
	 * v1.0 zhanghc 2022年11月9日下午3:01:00
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);

	/**
	 * 试卷分配
	 * 
	 * v1.0 zhanghc 2023年2月23日下午2:33:26
	 * 
	 * @param examId
	 * @param num    void
	 */
	void assign(Integer examId, Integer num);

	/**
	 * 试卷
	 * 
	 * v1.0 zhanghc 2024年5月21日下午12:19:17
	 * 
	 * @param examId
	 * @param userId
	 * @return List<PaperPart>
	 */
	List<PaperPart> paper(Integer examId, Integer userId);

	/**
	 * 试卷处理（主要用途是注解缓存，必须有接口才可以）
	 * 
	 * v1.0 zhanghc 2024年5月21日下午12:19:25
	 * 
	 * @param examId
	 * @param userId void
	 */
	void paperHandle(Integer examId, Integer userId);

	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2023年1月30日下午6:32:13
	 * 
	 * @param examId
	 * @return List<MyMark>
	 */
	List<MyMark> getList(Integer examId);

	/**
	 * 打分
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * 
	 * @param examId     考试ID
	 * @param userId     考试用户ID
	 * @param questionId 试题ID
	 * @param userScore  用户得分
	 */
	void score(Integer examId, Integer userId, Integer questionId, BigDecimal userScore);

	/**
	 * 阅卷
	 * 
	 * v1.0 chenyun 2021年8月24日上午9:46:01
	 * 
	 * @param examId
	 * @param userId void
	 */
	void finish(Integer examId, Integer userId);
}
