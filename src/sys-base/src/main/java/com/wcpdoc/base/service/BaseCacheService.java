package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.entity.User;

/**
 * 基础缓存服务层接口
 * 
 * v1.0 zhanghc 2024年4月14日下午9:36:00
 */
public interface BaseCacheService {

	/**
	 * 用户
	 * 
	 * v1.0 zhanghc 2024年3月22日下午6:52:13
	 * 
	 * @return User
	 */
	User getUser(Integer id);

	/**
	 * 机构
	 * 
	 * v1.0 zhanghc 2024年3月22日下午6:52:13
	 * 
	 * @return User
	 */
	Org getOrg(Integer id);

	/**
	 * 参数
	 * 
	 * v1.0 zhanghc 2024年5月22日下午2:24:21
	 * 
	 * @return Parm
	 */
	Parm getParm();
}
