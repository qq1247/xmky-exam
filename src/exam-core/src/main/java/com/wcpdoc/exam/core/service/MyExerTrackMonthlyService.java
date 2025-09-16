package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExerTrackMonthly;

/**
 * 我的练习跟踪月度服务层接口
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
public interface MyExerTrackMonthlyService extends BaseService<MyExerTrackMonthly> {

	/**
	 * 我的练习跟踪月度
	 * 
	 * v1.0 zhanghc 2025年9月12日下午8:31:53
	 * 
	 * @param exerId
	 * @param userId
	 * @param ym
	 * @return MyExerTrackMonthly
	 */
	MyExerTrackMonthly getMyExerTrackMonthly(Integer exerId, Integer userId, Integer ym);

	/**
	 * 我的练习跟踪月度列表
	 * 
	 * v1.0 zhanghc 2025年9月12日下午9:59:08
	 * 
	 * @param exerId
	 * @param userId
	 * @param startYm
	 * @param endYm
	 * @return List<MyExerTrackMonthly>
	 */
	List<MyExerTrackMonthly> getList(Integer exerId, Integer startYm, Integer endYm);

	/**
	 * 我的练习跟踪月度列表
	 * 
	 * v1.0 zhanghc 2025年9月12日下午9:59:08
	 * 
	 * @param exerId
	 * @param userId
	 * @param startYm
	 * @param endYm
	 * @return List<MyExerTrackMonthly>
	 */
	List<MyExerTrackMonthly> getList(Integer exerId, Integer userId, Integer startYm, Integer endYm);
}
