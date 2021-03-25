package com.wcpdoc.exam.base.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;

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
	 * 获取权限
	 * 
	 * v1.0 zhanghc 2016年8月12日上午10:57:35
	 * 
	 * @param id
	 * @return Map<Integer, Long>
	 */
	Map<Integer, Long> getAuth(Integer id);

	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午3:09:25
	 * 
	 * @param id
	 * @param newPwd
	 * void
	 */
	void doPwdUpdate(Integer id, String newPwd);

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
	 * 获取岗位列表
	 * 
	 * v1.0 zhanghc 2021年3月3日下午2:00:19
	 * @param id
	 * @return List<Post>
	 */
	List<Post> getPostList(Integer id);

	/**
	 * 修改岗位
	 * 
	 * v1.0 chenyun 2021年3月16日下午5:22:45
	 * @param id
	 * @param postId void
	 */
	void postUpdate(Integer id, Integer[] postId);
	
	/**
	 * 在线用户
	 * 
	 * v1.0 chenyun 2021年3月25日上午10:29:51
	 * @return PageOut
	 */
	PageOut onList();
}
