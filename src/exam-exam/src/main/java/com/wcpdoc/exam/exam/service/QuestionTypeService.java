package com.wcpdoc.exam.exam.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
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

	/**
	 * 获取组织机构
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
	 * @param user
	 * void
	 */
	void doAuthUserAdd(Integer id, Integer[] userIds, LoginUser user);

	/**
	 * 完成删除权限用户
	 * 
	 * v1.0 zhanghc 2018年5月30日下午7:08:01
	 * @param id 
	 * @param userIds 
	 * @param user 
	 * void
	 */
	void doAuthUserDel(Integer id, Integer[] userIds, LoginUser user);
}
