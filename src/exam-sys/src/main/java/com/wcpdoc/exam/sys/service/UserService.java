package com.wcpdoc.exam.sys.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.Org;
import com.wcpdoc.exam.sys.entity.User;
/**
 * 用户服务层接口
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
public interface UserService extends BaseService<User>{
	
	/**
	 * 保存用户
	 * v1.0 zhanghc 2016年7月13日下午5:17:52
	 * @param user
	 * void
	 */
	void saveAndUpdate(User user);

	/**
	 * 修改用户
	 * v1.0 zhanghc 2016年7月13日下午5:24:54
	 * @param user
	 * void
	 */
	void editAndUpdate(User user);
	
	/**
	 * 删除用户
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @param ids
	 * void
	 */
	void delAndUpdate(Integer[] ids);
	
	/**
	 * 获取组织机构树
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getOrgTreeList();
	
	/**
	 * 完成设置组织机构
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @param ids 用户ID
	 * @param orgId 组织机构ID
	 * @return PageResult
	 */
	void doOrgUpdate(Integer[] ids, Integer orgId);

	/**
	 * 获取岗位树
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @param ids
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getPostUpdatePostTreeList(Integer[] ids);
	
	/**
	 * 完成设置岗位
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @param ids 用户ID
	 * @param postIds 岗位ID
	 * @return PageResult
	 */
	void doPostUpdate(Integer[] ids, Integer[] postIds);
	
	/**
	 * 获取用户
	 * v1.0 zhanghc 2016年7月18日下午3:30:47
	 * @param loginName
	 * void
	 */
	User getUser(String loginName);

	/**
	 * 获取用户列表
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @param orgId
	 * @return List<User>
	 */
	List<User> getList(Integer orgId);
	
	/**
	 * 获取组织机构
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * @param id 用户ID
	 * @return Org
	 */
	Org getOrg(Integer id);

	/**
	 * 获取权限总和
	 * v1.0 zhanghc 2016年8月12日上午10:57:35
	 * @param id 用户id
	 * @return Map<Integer, Long>
	 */
	Map<Integer, Long> getAuthSum(Integer id);

	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午3:09:25
	 * @param id
	 * @param newPwd
	 * void
	 */
	void doPwdUpdate(Integer id, String newPwd);

	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午4:41:55
	 * @param id
	 * @param oldPwd
	 * @param newPwd
	 * void
	 */
	void doPwdUpdate(Integer id, String oldPwd, String newPwd);

	/**
	 * 获取加密后的密码
	 * 
	 * v1.0 zhanghc 2017年7月17日下午4:11:03
	 * @param loginName
	 * @param pwd
	 * @return String
	 */
	String getEncryptPwd(String loginName, String pwd);
}
