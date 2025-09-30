package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyFavQuestion;

/**
 * 我的收藏试题服务层接口
 * 
 * v1.0 zhanghc 2025年9月26日下午9:51:28
 */
public interface MyFavQuestionService extends BaseService<MyFavQuestion> {

	/**
	 * 我的收藏试题获取
	 * 
	 * v1.0 zhanghc 2025年9月26日下午10:34:46
	 * 
	 * @param userId
	 * @param questionId
	 * @return MyFavQuestion
	 */
	MyFavQuestion getMyFavQuestion(Integer userId, Integer questionId);

	/**
	 * 我的收藏试题列表
	 * 
	 * v1.0 zhanghc 2025年9月27日上午12:32:00
	 * 
	 * @param userId
	 * @return List<MyFavQuestion>
	 */
	List<MyFavQuestion> getList(Integer userId);

}
