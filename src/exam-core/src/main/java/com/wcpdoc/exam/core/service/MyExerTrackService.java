package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExerTrack;

/**
 * 我的练习跟踪服务层接口
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
public interface MyExerTrackService extends BaseService<MyExerTrack> {

	/**
	 * 我的练习跟踪
	 * 
	 * v1.0 zhanghc 2025年9月8日下午11:46:14
	 * 
	 * @param exerId
	 * @param userId
	 * @param ymd    yyyyMMdd
	 * @return MyExerTrack
	 */
	MyExerTrack getMyExerTrack(Integer exerId, Integer userId, Integer ymd);

	/**
	 * 我的练习跟踪列表
	 * 
	 * v1.0 zhanghc 2025年9月9日下午1:26:18
	 * 
	 * @param exerId
	 * @param userId
	 * @param startDay
	 * @param endDay
	 * @return List<MyExerTrack>
	 */
	List<MyExerTrack> getList(Integer exerId, Integer userId, Integer startDate, Integer endDate);

}
