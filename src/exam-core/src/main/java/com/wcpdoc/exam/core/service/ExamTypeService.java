package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;
/**
 * 考试分类服务层接口
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
public interface ExamTypeService extends BaseService<ExamType>{
	
	/**
	 * 添加考试分类
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param examType
	 * void
	 */
	void addAndUpdate(ExamType examType);

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
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2018年5月29日下午11:23:13
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getOrgTreeList();

	/**
	 * 获取权限用户
	 * 
	 * v1.0 zhanghc 2018年5月29日下午11:24:14
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getAuthUserListpage(PageIn pageIn);

	/**
	 * 获取权限用户
	 * 
	 * v1.0 zhanghc 2018年5月30日下午6:28:19
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getAuthUserAddList(PageIn pageIn);
	
	/**
	 * 完成添加权限用户
	 * 
	 * v1.0 zhanghc 2018年5月30日下午7:08:26
	 * @param id
	 * @param userIds
	 * @param syn2Sub 
	 * @param user
	 * void
	 */
	void doAuthUserAdd(Integer id, Integer[] userIds, boolean syn2Sub, LoginUser user);

	/**
	 * 完成删除权限用户
	 * 
	 * v1.0 zhanghc 2018年5月30日下午7:08:01
	 * @param id 
	 * @param userIds 
	 * @param syn2Sub 
	 * @param user 
	 * void
	 */
	void doAuthUserDel(Integer id, Integer[] userIds, boolean syn2Sub, LoginUser user);

	/**
	 * 完成添加权限机构
	 * 
	 * v1.0 zhanghc 2018年5月31日下午9:45:51
	 * @param id
	 * @param orgIds
	 * @param syn2Sub 
	 * @param currentUser void
	 */
	void doAuthOrgUpdate(Integer id, Integer[] orgIds, boolean syn2Sub, LoginUser user);

	/**
	 * 获取机构岗位树
	 * 
	 * v1.0 zhanghc 2018年6月1日下午12:13:39
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getOrgPostTreeList();

	/**
	 * 完成添加权限岗位
	 * 
	 * v1.0 zhanghc 2018年6月1日下午12:20:02
	 * @param id
	 * @param postIds
	 * @param syn2Sub 
	 * @param user 
	 * void
	 */
	void doAuthPostUpdate(Integer id, Integer[] postIds, boolean syn2Sub, LoginUser user);

	/**
	 * 获取考试分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:19:02
	 * @return List<ExamType>
	 */
	List<ExamType> getList();
}
