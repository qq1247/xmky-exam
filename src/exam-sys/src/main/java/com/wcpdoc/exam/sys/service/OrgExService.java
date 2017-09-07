package com.wcpdoc.exam.sys.service;

import com.wcpdoc.exam.sys.entity.Org;

/**
 * 组织机构扩展服务层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgExService{

	/**
	 * 删除关联引用
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * @param org
	 * void
	 */
	void delAndUpdate(Org org);
	
}

