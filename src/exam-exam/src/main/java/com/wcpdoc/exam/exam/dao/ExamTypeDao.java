package com.wcpdoc.exam.exam.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.entity.ExamType;

/**
 * 考试分类数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeDao extends BaseDao<ExamType>{

	/**
	 * 获取考试分类树
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

	/**
	 * 获取所有子考试分类列表，包括自己
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id 
	 * void
	 */
	List<ExamType> getAllSubExamTypeList(Integer id);
	
	/**
	 * 获取考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param name
	 * @return ExamType
	 */
	ExamType getExamTypeByName(String name);

	/**
	 * 获取权限用户
	 * 
	 * v1.0 zhanghc 2018年5月29日下午11:26:22
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getAuthUserListpage(PageIn pageIn);

	/**
	 * 获取权限用户
	 * 
	 * v1.0 zhanghc 2018年5月29日下午11:26:22
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getAuthUserAddList(PageIn pageIn);

	/**
	 * 获取考试分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:20:49
	 * @return List<ExamType>
	 */
	List<ExamType> getList();

	/**
	 * 获取考试分类列表
	 * 
	 * v1.0 zhanghc 2018年6月6日下午10:03:33
	 * @param parentId
	 * @return List<ExamType>
	 */
	List<ExamType> getList(Integer parentId);
}
