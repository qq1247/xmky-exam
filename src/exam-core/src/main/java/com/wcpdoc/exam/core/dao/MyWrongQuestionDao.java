package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyWrongQuestion;

/**
 * 我的错题数据访问层接口
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
public interface MyWrongQuestionDao extends RBaseDao<MyWrongQuestion> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		return null;
	}

	/**
	 * 我的错题获取
	 * 
	 * v1.0 zhanghc 2025年9月26日下午10:34:46
	 * 
	 * @param userId
	 * @param questionId
	 * @return MyWrongQuestion
	 */
	default MyWrongQuestion getMyWrongQuestion(Integer userId, Integer questionId) {
		return selectOne(new LambdaQueryWrapper<MyWrongQuestion>().eq(MyWrongQuestion::getUserId, userId)
				.eq(MyWrongQuestion::getQuestionId, questionId));
	}

	/**
	 * 我的错题列表
	 * 
	 * v1.0 zhanghc 2025年9月27日上午12:07:46
	 * 
	 * @param userId
	 * @return List<MyWrongQuestion>
	 */
	default List<MyWrongQuestion> getList(Integer userId) {
		return selectList(new LambdaQueryWrapper<MyWrongQuestion>().eq(MyWrongQuestion::getUserId, userId).eq(MyWrongQuestion::getState, 2));
	}

}
