package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyFavQuestion;

/**
 * 我的收藏试题数据访问层接口
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
public interface MyFavQuestionDao extends RBaseDao<MyFavQuestion> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		return null;
	}

	/**
	 * 我的收藏试题获取
	 * 
	 * v1.0 zhanghc 2025年9月26日下午10:34:46
	 * 
	 * @param userId
	 * @param questionId
	 * @return MyFavQuestion
	 */
	default MyFavQuestion getMyFavQuestion(Integer userId, Integer questionId) {
		return selectOne(new LambdaQueryWrapper<MyFavQuestion>().eq(MyFavQuestion::getUserId, userId)
				.eq(MyFavQuestion::getQuestionId, questionId));
	}

	/**
	 * 我的收藏试题列表
	 * 
	 * v1.0 zhanghc 2025年9月27日上午12:32:00
	 * 
	 * @param userId
	 * @return List<MyFavQuestion>
	 */
	default List<MyFavQuestion> getList(Integer userId) {
		return selectList(new LambdaQueryWrapper<MyFavQuestion>().eq(MyFavQuestion::getUserId, userId));
	}
}
