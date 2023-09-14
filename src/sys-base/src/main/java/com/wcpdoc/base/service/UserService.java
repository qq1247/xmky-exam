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
	 * 用户添加
	 * 
	 * v1.0 zhanghc 2023年9月4日上午10:23:40
	 * @param user void
	 */
	void addEx(User user);
	
	/**
	 * 用户修改
	 * 
	 * v1.0 zhanghc 2023年9月4日上午10:49:14
	 * @param user void
	 */
	void editEx(User user);
	
	/**
	 * 用户删除
	 * 
	 * v1.0 zhanghc 2023年9月4日下午4:36:22
	 * @param id void
	 */
	void delEx(Integer id);

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
	 * 密码初始化
	 * 
	 * v1.0 zhanghc 2017年7月14日下午3:09:25
	 * 
	 * @param id
	 * @param newPwd
	 * String
	 */
	String pwdInit(Integer id);

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
	 * 登录账号是否存在
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
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2021年11月5日上午10:38:37
	 * @param ids
	 * @return List<User>
	 */
	List<User> getList(Integer[] ids);
	
	/**
	 * 用户冻结
	 * 
	 * v1.0 chenyun 2022年04月21日下午18:48:00
	 * @param ids void
	 */
	void frozen(Integer id);

	/**
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2022年5月10日下午4:54:44
	 * @return List<User>
	 */
	List<User> getList();


}
