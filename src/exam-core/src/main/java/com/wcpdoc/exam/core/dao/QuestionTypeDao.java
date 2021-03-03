package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 试题分类数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeDao extends BaseDao<QuestionType> {
	/**
	 * 获取试题分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:20:49
	 * @return List<QuestionType>
	 */
	List<QuestionType> getList();

	/**
	 * 获取试题分类列表
	 * 
	 * v1.0 zhanghc 2018年6月6日下午10:03:33
	 * @param parentId
	 * @return List<QuestionType>
	 */
	List<QuestionType> getList(Integer parentId);
	
	/**
	 * 名称是否存在
	 * 
	 * v1.0 zhanghc 2020-09-05 10:12:16
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	boolean existName(String name, Integer excludeId);

	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getUserListpage(PageIn pageIn);
}
