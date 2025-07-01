package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyExerQuestion;

/**
 * 我的练习试题数据访问层接口
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
public interface MyExerQuestionDao extends RBaseDao<MyExerQuestion> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		return null;
	}

	/**
	 * 我的练习列表
	 * 
	 * v1.0 zhanghc 2025年6月15日下午2:57:26
	 * 
	 * @param exerId
	 * @param userId
	 * @return List<MyExer>
	 */
	default List<MyExerQuestion> getList(Integer exerId, Integer userId) {
		return selectList(new LambdaQueryWrapper<MyExerQuestion>().eq(MyExerQuestion::getExerId, exerId)
				.eq(MyExerQuestion::getUserId, userId));
	}

	/**
	 * 我的练习获取
	 * 
	 * v1.0 zhanghc 2025年6月16日下午9:11:49
	 * 
	 * @param exerId
	 * @param userId
	 * @param questionId
	 * @return MyExerQuestion
	 */
	default MyExerQuestion getMyExerQuestion(Integer exerId, Integer userId, Integer questionId) {
		return selectOne(new LambdaQueryWrapper<MyExerQuestion>().eq(MyExerQuestion::getExerId, exerId)
				.eq(MyExerQuestion::getUserId, userId).eq(MyExerQuestion::getQuestionId, questionId));
	}
}
