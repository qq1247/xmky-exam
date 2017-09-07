package com.wcpdoc.exam.exam.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.exam.entity.PaperType;

/**
 * 试卷分类数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
public interface PaperTypeDao extends BaseDao<PaperType>{

	/**
	 * 获取试卷分类树型列表
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();

	/**
	 * 移动试卷分类
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param sourceId
	 * @param targetId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 获取所有子试卷分类列表，包括自己
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param id 
	 * void
	 */
	List<PaperType> getAllSubPaperTypeList(Integer id);
	
	/**
	 * 获取试卷分类
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param name
	 * @return PaperType
	 */
	PaperType getPaperTypeByName(String name);
}
