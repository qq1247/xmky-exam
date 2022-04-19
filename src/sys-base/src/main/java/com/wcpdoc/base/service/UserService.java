package com.wcpdoc.base.service;

import java.util.List;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.core.service.BaseService;

/**
 * 用户服务层接口
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
public interface UserService extends BaseService<User> {

	/**
	 * 获取用户 
	 * 
	 * v1.0 zhanghc 2016年7月18日下午3:30:47
	 * 
	 * @param loginName
	 * void
	 */
	User getUser(String loginName);

	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午3:09:25
	 * 
	 * @param id
	 * @param newPwd
	 * String
	 */
	String doPwdUpdate(Integer id);

	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午4:41:55
	 * 
	 * @param id
	 * @param oldPwd
	 * @param newPwd
	 * void
	 */
	void pwdUpdate(String oldPwd, String newPwd);

	/**
	 * 获取加密后的密码
	 * 
	 * v1.0 zhanghc 2017年7月17日下午4:11:03
	 * 
	 * @param loginName
	 * @param pwd
	 * @return String
	 */
	String getEncryptPwd(String loginName, String pwd);

	/**
	 * 登录名称是否存在
	 * 
	 * v1.0 zhanghc 2020年6月18日上午11:35:32
	 * @param user
	 * @return boolean
	 */
	boolean existLoginName(User user);

	/**
	 * 获取用户列表 
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param orgId
	 * @return List<User>
	 */
	List<User> getList(Integer orgId);

	/**
	 * 修改角色
	 * 
	 * v1.0 chenyun 2021年3月16日下午5:22:45
	 * @param id
	 * @param roles void
	 */
	void roleUpdate(Integer id, String[] roles);
	
	/**
	 * 同步用户
	 * 
	 * v1.0 chenyun 2021年3月26日下午3:50:32
	 * @param user
	 * @param orgId
	 */
	void syncUser(List<User> user, Integer orgId);

	/**
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2021年11月5日上午10:38:37
	 * @param ids
	 * @return List<User>
	 */
	List<User> getList(Integer[] ids);
}
