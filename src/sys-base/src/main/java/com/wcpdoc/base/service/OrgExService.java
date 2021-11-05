package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.Org;

/**
 * 组织机构扩展服务层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgExService {

	/**
	 * 删除关联引用 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * void
	 */
	void delAndUpdate(Org org);

	/**
	 * 添加用户 
	 * v1.0 chenyun 2020-6-3 13:20:53
	 * 
	 * @param phone
	 * @param pwd
	 * void
	 */
	void addAndUpdate(Org org, String phone, String pwd);
}
