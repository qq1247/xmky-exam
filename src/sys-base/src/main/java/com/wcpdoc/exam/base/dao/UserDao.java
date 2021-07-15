package com.wcpdoc.exam.base.dao;

import java.util.List;

import com.wcpdoc.exam.base.entity.Post;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.core.dao.RBaseDao;

/**
 * 用户数据访问层接口
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
public interface UserDao extends RBaseDao<User> {

	/**
	 * 获取用户 
	 * 
	 * v1.0 zhanghc 2016年7月13日下午5:12:23
	 * 
	 * @param loginName
	 * @return User
	 */
	User getUser(String loginName);

	/**
	 * 登录名称是否存在
	 * 
	 * v1.0 zhanghc 2020年6月15日上午10:51:04
	 * @param loginName
	 * @param excludeId
	 * @return boolean
	 */
	boolean existLoginName(String loginName, Integer excludeId);

	/**
	 * 获取岗位列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午2:13:04
	 * @param id
	 * @return List<Post>
	 */
	List<Post> getPostList(Integer id);

	/**
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2020年8月26日下午6:29:50
	 * @param orgId
	 * @return List<User>
	 */
	List<User> getList(Integer orgId);
}
