package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.QuestionAnswer;

/**
 * 试题答案数据访问层接口
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
public interface QuestionAnswerDao extends RBaseDao<QuestionAnswer> {

	/**
	 * 查询试题答案列表
	 * 
	 * v1.0 chenyun 2021年7月20日上午10:29:11
	 * 
	 * @param questionId
	 * @return List<QuestionAnswer>
	 */
	default List<QuestionAnswer> getList(Integer questionId) {
		return selectList(new LambdaQueryWrapper<QuestionAnswer>().eq(QuestionAnswer::getQuestionId, questionId)
				.orderByAsc(QuestionAnswer::getNo));
	}
}
