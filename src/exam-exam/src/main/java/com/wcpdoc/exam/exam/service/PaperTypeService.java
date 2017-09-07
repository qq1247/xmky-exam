package com.wcpdoc.exam.exam.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.PaperType;
/**
 * 试题分类服务层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperTypeService extends BaseService<PaperType>{
	
	/**
	 * 保存试题分类
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param paperType
	 * void
	 */
	void saveAndUpdate(PaperType paperType);

	/**
	 * 修改试题分类
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param paperType
	 * void
	 */
	void editAndUpdate(PaperType paperType);

	/**
	 * 删除试题分类
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param ids 
	 * void
	 */
	void delAndUpdate(Integer[] ids);
	
	/**
	 * 获取试题分类树型列表
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();

	/**
	 * 移动试题分类
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param sourceId
	 * @param targetId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);
}
