package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyQuestion;
/**
 * 我的试题服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyQuestionService extends BaseService<MyQuestion>{
	/**
	 * 获取我的试题
	 * 
	 * v1.0 zhanghc 2021年10月19日上午9:54:36
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return MyQuestion
	 */
	MyQuestion getMyQuestion(Integer examId, Integer userId, Integer questionId);

	/**
	 * 获取我的试题
	 * 
	 * v1.0 zhanghc 2022年5月20日上午9:18:55
	 * @param examId
	 * @param userId
	 * @return List<MyQuestion>
	 */
	List<MyQuestion> getList(Integer examId, Integer userId);

	/**
	 * 获取我的试题列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param examId
	 * @param userId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getAnswerList(Integer examId, Integer userId);
}
