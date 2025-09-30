package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyExerTrack;

/**
 * 我的练习跟踪数据访问层接口
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
public interface MyExerTrackDao extends RBaseDao<MyExerTrack> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		return null;
	}

	/**
	 * 我的练习跟踪
	 * 
	 * v1.0 zhanghc 2025年9月8日下午11:46:14
	 * 
	 * @param userId
	 * @param exerId
	 * @param ymd    yyyyMMdd
	 * @return MyExerTrack
	 */
	default MyExerTrack getMyExerTrack(Integer exerId, Integer userId, Integer ymd) {
		return selectOne(new LambdaQueryWrapper<MyExerTrack>().eq(MyExerTrack::getExerId, exerId)
				.eq(MyExerTrack::getUserId, userId).eq(MyExerTrack::getYmd, ymd));
	}

	/**
	 * 我的练习跟踪列表
	 * 
	 * v1.0 zhanghc 2025年9月9日下午1:26:18
	 * 
	 * @param exerId
	 * @param userId
	 * @param startDay yyyyMMdd
	 * @param endDay   yyyyMMdd
	 * @return List<MyExerTrack>
	 */
	default List<MyExerTrack> getList(Integer exerId, Integer userId, Integer startDate, Integer endDate) {
		return selectList(new LambdaQueryWrapper<MyExerTrack>().eq(MyExerTrack::getExerId, exerId)
				.eq(MyExerTrack::getUserId, userId).ge(MyExerTrack::getYmd, startDate)
				.le(MyExerTrack::getYmd, endDate));
	}

}
