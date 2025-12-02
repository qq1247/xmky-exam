package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.RegistUser;
import com.wcpdoc.core.service.BaseService;

/**
 * 注册用户服务层接口
 * 
 * v1.0 zhanghc 2025年12月1日下午7:02:37
 */
public interface RegistUserService extends BaseService<RegistUser> {

	boolean existLoginName(String loginName);

	/**
	 * 注册用户同意
	 * 
	 * v1.0 zhanghc 2025年12月1日下午7:06:04
	 * 
	 * @param id
	 * @param remark
	 * @return PageResult
	 */
	void approve(Integer id, String remark);
	
}
