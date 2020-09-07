package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.QuestionType;

/**
 * 试题分类数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionTypeDao extends BaseDao<QuestionType>{

	/**
	 * 获取试题分类树
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();
	
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
}
