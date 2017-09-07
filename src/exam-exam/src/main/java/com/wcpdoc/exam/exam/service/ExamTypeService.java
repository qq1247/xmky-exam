package com.wcpdoc.exam.exam.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.ExamType;
/**
 * 考试分类服务层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeService extends BaseService<ExamType>{
	
	/**
	 * 保存考试分类
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param examType
	 * void
	 */
	void saveAndUpdate(ExamType examType);

	/**
	 * 修改考试分类
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param examType
	 * void
	 */
	void editAndUpdate(ExamType examType);

	/**
	 * 删除考试分类
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param ids 
	 * void
	 */
	void delAndUpdate(Integer[] ids);
	
	/**
	 * 获取考试分类树型列表
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();

	/**
	 * 移动考试分类
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param sourceId
	 * @param targetId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);
}
