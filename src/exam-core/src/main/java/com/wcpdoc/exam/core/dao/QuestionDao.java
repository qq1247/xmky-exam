package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionDao extends BaseDao<Question>{

	/**
	 * 获取试题分类
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return QuestionType
	 */
	QuestionType getQuestionType(Integer id);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:44:51
	 * @param questionTypeId
	 * @return List<Question>
	 */
	List<Question> getList(Integer questionTypeId);
	
	/**
	 * 试题统计(类型和难易程度)
	 * 
	 * v1.0 chenyun 2021年3月23日下午2:53:07
	 * @param questionTypeId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> statisticsTypeDifficulty(Integer questionTypeId);
	
	/**
	 * 试题统计
	 * 
	 * v1.0 chenyun 2021年3月23日下午2:53:07
	 * @param examId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> accuracy(Integer examId);
}
