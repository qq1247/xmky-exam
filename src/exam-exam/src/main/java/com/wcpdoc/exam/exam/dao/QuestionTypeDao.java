package com.wcpdoc.exam.exam.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.exam.entity.QuestionType;

/**
 * 试题分类数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeDao extends BaseDao<QuestionType>{

	/**
	 * 获取试题分类树型列表
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();

	/**
	 * 移动试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param sourceId
	 * @param targetId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 获取所有子试题分类列表，包括自己
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id 
	 * void
	 */
	List<QuestionType> getAllSubQuestionTypeList(Integer id);
	
	/**
	 * 获取试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param name
	 * @return QuestionType
	 */
	QuestionType getQuestionTypeByName(String name);
}
