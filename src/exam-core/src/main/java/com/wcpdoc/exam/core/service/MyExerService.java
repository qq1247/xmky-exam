package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExer;
import com.wcpdoc.exam.core.entity.MyExerQuestion;

/**
 * 我的练习服务层接口
 * 
 * v1.0 zhanghc 2025年6月8日下午9:22:47
 */
public interface MyExerService extends BaseService<MyExer> {

	/**
	 * 我的练习拉取
	 * 
	 * v1.0 zhanghc 2025年6月15日上午10:04:47
	 * 
	 * @param exerId
	 * @param userId
	 * @return Map<String, Object>
	 */
	Map<String, Object> pull(Integer exerId, Integer userId);
	
	/**
	 * 我的练习生成
	 * 
	 * v1.0 zhanghc 2025年6月23日下午3:52:21
	 * @param exerId
	 * @param userId
	 * @param type
	 * @return List<MyExerQuestion>
	 */
	List<MyExerQuestion> generate(Integer exerId, Integer userId, Integer type);

	/**
	 * 我的练习列表
	 * 
	 * v1.0 zhanghc 2025年6月23日下午3:23:16
	 * 
	 * @param exerId
	 * @param userId
	 * @return List<MyExer>
	 */
	List<MyExer> getList(Integer exerId, Integer userId);

	/**
	 * 我的练习答题
	 * 
	 * v1.0 zhanghc 2025年6月17日下午11:04:03
	 * 
	 * @param exerId
	 * @param userId
	 * @param questionId
	 * @param userAnswers
	 * @param userScore
	 * @return BigDecimal
	 */
	BigDecimal answer(Integer exerId, Integer userId, Integer questionId, String[] userAnswers, BigDecimal userScore);

	/**
	 * 重新练习
	 * 
	 * v1.0 zhanghc 2025年6月25日下午12:27:39
	 * 
	 * @param exerId
	 * @param type
	 * @return PageResult
	 */
	void exerReset(Integer exerId, Integer id, Integer type);
}
