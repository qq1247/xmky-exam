package com.wcpdoc.core.service;

import com.wcpdoc.core.entity.OnlineUser;

/**
 * 在线用户服务层接口
 * 
 * v1.0 zhanghc 2021年10月15日下午2:02:42
 */
public interface OnlineUserService {

	/**
	 * 强制退出登录
	 * 
	 * v1.0 zhanghc 2021年10月18日下午3:37:16
	 * 
	 * @param userId void
	 */
	void out(Integer userId);

	OnlineUser getEntity(Integer id);

	void update(OnlineUser onlineUser);

	void add(OnlineUser onlineUser);

	void del(Integer id);

	void del(Integer[] ids);

}
