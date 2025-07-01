package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExerQuestion;

/**
 * 我的练习试题服务层接口
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
public interface MyExerQuestionService extends BaseService<MyExerQuestion> {

	/**
	 * 我的练习列表
	 * 
	 * v1.0 zhanghc 2025年6月15日下午2:56:42
	 * @param exerId
	 * @param userId
	 * @return List<MyExer>
	 */
	List<MyExerQuestion> getList(Integer exerId, Integer userId);

	/**
	 * 我的练习获取
	 * 
	 * v1.0 zhanghc 2025年6月16日下午9:10:48
	 * @param exerId
	 * @param userId
	 * @param questionId
	 * @return MyExerQuestion
	 */
	MyExerQuestion getMyExerQuestion(Integer exerId, Integer userId, Integer questionId);

}
