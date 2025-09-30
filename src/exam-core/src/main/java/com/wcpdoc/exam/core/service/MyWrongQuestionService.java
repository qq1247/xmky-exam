package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyWrongQuestion;

/**
 * 我的错题服务层接口
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
public interface MyWrongQuestionService extends BaseService<MyWrongQuestion> {

	/**
	 * 我的错题获取
	 * 
	 * v1.0 zhanghc 2025年9月26日下午10:44:26
	 * 
	 * @param userId
	 * @param questionId
	 * @return MyWrongQuestion
	 */
	MyWrongQuestion getMyWrongQuestion(Integer userId, Integer questionId);

	/**
	 * 我的错题列表
	 * 
	 * v1.0 zhanghc 2025年9月27日上午12:07:46
	 * 
	 * @param userId
	 * @return List<MyWrongQuestion>
	 */
	List<MyWrongQuestion> getList(Integer userId);

}
