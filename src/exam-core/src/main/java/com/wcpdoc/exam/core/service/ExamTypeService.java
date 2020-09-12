package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
/**
 * 考试分类服务层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeService extends BaseService<ExamType> {
	
	/**
	 * 添加试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param examType
	 * void
	 */
	void addAndUpdate(ExamType examType);

	/**
	 * 删除试题分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 获取试题分类树
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
	 * 名称是否重复
	 * 
	 * v1.0 zhanghc 2020-09-05 10:12:16
	 * @param examType
	 * @return boolean
	 */
	boolean existName(ExamType examType);

	/**
	 * 获取试题分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:19:02
	 * @return List<ExamType>
	 */
	List<ExamType> getList();

	/**
	 * 完成授权
	 * 
	 * v1.0 zhanghc 2020年9月8日上午10:06:53
	 * @param id
	 * @param userIds
	 * @param postIds
	 * @param orgIds
	 * @param syn2Sub true ： 同步授权到子分类
	 * void
	 */
	void doAuth(Integer id, Integer[] userIds, Integer[] postIds, Integer[] orgIds, boolean syn2Sub);

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

}
