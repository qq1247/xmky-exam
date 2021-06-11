package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 考试分类数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeDao extends BaseDao<ExamType> {

	/**
	 * 获取试题分类树
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();
	
	/**
	 * 获取试题分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:20:49
	 * @return List<ExamType>
	 */
	List<ExamType> getList();

	/**
	 * 获取试题分类列表
	 * 
	 * v1.0 zhanghc 2018年6月6日下午10:03:33
	 * @param parentId
	 * @return List<ExamType>
	 */
	List<ExamType> getList(Integer parentId);
	
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
	 * 获取权限用户列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getAuthUserListpage(PageIn pageIn);

	/**
	 * 获取权限岗位列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getAuthPostListpage(PageIn pageIn);

	/**
	 * 获取权限机构列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getAuthOrgListpage(PageIn pageIn);

	/**
	 * 获取人员列表
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut authUserListpage(PageIn pageIn);
}
