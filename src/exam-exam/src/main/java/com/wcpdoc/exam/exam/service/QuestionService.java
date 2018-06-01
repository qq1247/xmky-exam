package com.wcpdoc.exam.exam.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.entity.QuestionType;
/**
 * 试题服务层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionService extends BaseService<Question>{

	/**
	 * 获取试题分类树
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getQuestionTypeTreeList();

	/**
	 * 获取试题分类
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id 
	 * @return QuestionType
	 */
	QuestionType getQuestionType(Integer id);

	/**
	 * 完成设置试题分类
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param ids 试题ID
	 * @param questionTypeId 试题分类ID
	 * @return PageResult
	 */
	void doQuestionTypeUpdate(Integer[] ids, Integer questionTypeId);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:43:55
	 * @param questionTypeId
	 * @return List<Question>
	 */
	List<Question> getList(Integer questionTypeId);

	/**
	 * 删除试题
	 * 
	 * v1.0 zhanghc 2017年8月6日下午9:43:55
	 * @param ids
	 * void
	 */
	void delAndUpdate(Integer[] ids);
	
}
