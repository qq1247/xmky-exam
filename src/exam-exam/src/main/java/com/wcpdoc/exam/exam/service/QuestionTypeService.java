package com.wcpdoc.exam.exam.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.exam.entity.QuestionTypeAuth;
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
	 * 完成保存权限机构
	 * 
	 * v1.0 zhanghc 2018年5月31日下午9:45:51
	 * @param id
	 * @param orgIds
	 * @param syn2Sub 
	 * @param currentUser void
	 */
	void doAuthOrgUpdate(Integer id, Integer[] orgIds, boolean syn2Sub, LoginUser user);

	/**
	 * 获取权限
	 * 
	 * v1.0 zhanghc 2018年5月31日下午10:12:54
	 * @param questionTypeAuthId
	 * @return QuestionTypeAuth
	 */
	QuestionTypeAuth getQuestionTypeAuth(Integer questionTypeAuthId);

	/**
	 * 获取机构岗位树
	 * 
	 * v1.0 zhanghc 2018年6月1日下午12:13:39
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getOrgPostTreeList();

	/**
	 * 完成保存权限岗位
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
	 * 获取试题分类列表
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:19:02
	 * @return List<QuestionType>
	 */
	List<QuestionType> getList();

	/**
	 * 获取试题分类权限列表
	 * 
	 * v1.0 zhanghc 2018年6月3日下午1:58:08
	 * @return List<QuestionTypeAuth>
	 */
	List<QuestionTypeAuth> getQuestionTypeAuthList();
}
