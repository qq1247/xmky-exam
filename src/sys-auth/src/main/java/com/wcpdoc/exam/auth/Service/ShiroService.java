package com.wcpdoc.exam.auth.Service;

import java.util.List;

import com.wcpdoc.exam.auth.entity.AuthUser;

/**
 * 权限服务层接口
 * 
 * v1.0 zhanghc 2021年3月19日下午4:43:18
 */
public interface ShiroService {

	/**
	 * 获取用户
	 * 
	 * v1.0 zhanghc 2021年3月19日下午4:51:46
	 * @param loginName
	 * @return User
	 */
	AuthUser getUser(String loginName);

	/**
	 * 获取角色列表
	 * 
	 * v1.0 zhanghc 2021年3月19日下午4:51:38
	 * @param userId
	 * @return List<String>
	 */
	List<String> getRoleList(Integer userId);
}
