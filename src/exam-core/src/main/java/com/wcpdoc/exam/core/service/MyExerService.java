package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExer;
import com.wcpdoc.exam.core.entity.MyExerTrack;
import com.wcpdoc.exam.core.entity.MyExerTrackMonthly;

/**
 * 我的练习服务层接口
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
public interface MyExerService extends BaseService<MyExer> {

	/**
	 * 我的练习添加
	 * 
	 * v1.0 zhanghc 2025年9月25日上午10:52:57
	 * 
	 * @param myExer void
	 */
	void add(MyExer myExer);

	/**
	 * 我的练习列表
	 * 
	 * v1.0 zhanghc 2025年6月23日下午3:23:16
	 * 
	 * @param exerId
	 * @param userId
	 * @return List<MyExer>
	 */
	List<MyExer> getList(Integer exerId, Integer userId);

	/**
	 * 我的练习答题
	 * 
	 * v1.0 zhanghc 2025年6月17日下午11:04:03
	 * 
	 * @param id
	 * @param questionId
	 * @param userAnswers
	 * @param userScore
	 * @return BigDecimal
	 */
	BigDecimal answer(Integer id, Integer questionId, String[] userAnswers, BigDecimal userScore);

	/**
	 * 我的练习跟踪
	 * 
	 * v1.0 zhanghc 2025年9月4日下午1:14:56
	 * 
	 * @param exerId
	 * @return PageResult
	 */
	void track(Integer exerId);

	/**
	 * 我的练习跟踪列表
	 * 
	 * v1.0 zhanghc 2025年9月9日下午3:34:37
	 * 
	 * @param exerId
	 * @param startDate yyyy-MM-dd
	 * @param endDate   yyyy-MM-dd
	 * @return List<MyExerTrack>
	 */
	List<MyExerTrack> getTrackList(Integer exerId, String startDate, String endDate);

	/**
	 * 我的练习跟踪月度列表
	 * 
	 * v1.0 zhanghc 2025年9月9日下午3:34:37
	 * 
	 * @param exerId
	 * @param startDate yyyy-MM
	 * @param endDate   yyyy-MM
	 * @return List<MyExerTrackMonthly>
	 */
	List<MyExerTrackMonthly> getTrackMonthlyList(Integer exerId, String startYm, String endYm);

}
