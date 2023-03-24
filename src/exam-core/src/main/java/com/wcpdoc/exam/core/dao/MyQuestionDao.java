package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.MyQuestion;

/**
 * 我的试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyQuestionDao extends BaseDao<MyQuestion>{
	
	/**
	 * 获取我的试题列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param myExamId
	 * @return List<MyQuestion>
	 */
	List<MyQuestion> getList(Integer examId, Integer userId);

	/**
	 * 获取我的试题
	 * 
	 * v1.0 zhanghc 2021年10月19日上午9:55:31
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return MyQuestion
	 */
	MyQuestion getMyQuestion(Integer examId, Integer userId, Integer questionId);

	/**
	 * 我的试题清理
	 * 
	 * v1.0 zhanghc 2023年3月22日下午5:40:35
	 * @param examId void
	 */
	void clear(Integer examId);
}
