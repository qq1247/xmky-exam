package com.wcpdoc.exam.exam.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.QuestionType;
/**
 * 试题分类服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeService extends BaseService<QuestionType>{
	
	/**
	 * 保存试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param questionType
	 * void
	 */
	void saveAndUpdate(QuestionType questionType);

	/**
	 * 修改试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param questionType
	 * void
	 */
	void editAndUpdate(QuestionType questionType);

	/**
	 * 删除试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param ids 
	 * void
	 */
	void delAndUpdate(Integer[] ids);
	
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
}
