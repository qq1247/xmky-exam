package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyExerTrackMonthly;

/**
 * 我的练习跟踪月度数据访问层接口
 * 
 * v1.0 zhanghc 2025年9月8日下午7:25:39
 */
public interface MyExerTrackMonthlyDao extends RBaseDao<MyExerTrackMonthly> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		return null;
	}

	/**
	 * 我的练习跟踪月度
	 * 
	 * v1.0 zhanghc 2025年9月12日下午8:33:55
	 * 
	 * @param exerId
	 * @param userId
	 * @param type
	 * @param ym
	 * @return MyExerTrackMonthly
	 */
	default MyExerTrackMonthly getMyExerTrackMonthly(Integer exerId, Integer userId, Integer ym) {
		return selectOne(new LambdaQueryWrapper<MyExerTrackMonthly>().eq(MyExerTrackMonthly::getExerId, exerId)
				.eq(MyExerTrackMonthly::getUserId, userId).eq(MyExerTrackMonthly::getYm, ym));
	}

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
	default List<MyExerTrackMonthly> getList(Integer exerId, Integer userId, Integer startYm, Integer endYm) {
		return selectList(new LambdaQueryWrapper<MyExerTrackMonthly>().eq(MyExerTrackMonthly::getExerId, exerId)
				.eq(MyExerTrackMonthly::getUserId, userId).ge(MyExerTrackMonthly::getYm, startYm)
				.le(MyExerTrackMonthly::getYm, endYm));
	}
}
