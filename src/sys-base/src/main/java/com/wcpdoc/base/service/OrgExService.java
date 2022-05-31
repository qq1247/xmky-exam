package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.Org;

/**
 * 机构扩展服务层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgExService {

	/**
	 * 删除机构
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * void
	 */
	void delAndUpdate(Org org);

	/**
	 * 导入机构（涉及到附件，不能直接用orgService）
	 * 
	 * v1.0 zhanghc 2022年5月10日上午11:17:52
	 * @param fileId 
	 * void
	 */
	void xlsImport(Integer fileId);
}
