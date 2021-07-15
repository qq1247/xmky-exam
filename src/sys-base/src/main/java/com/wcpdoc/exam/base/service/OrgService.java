package com.wcpdoc.exam.base.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.base.entity.Org;
import com.wcpdoc.exam.core.service.BaseService;

/**
 * 组织机构服务层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgService extends BaseService<Org> {

	/**
	 * 添加组织机构 
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @param phone
	 * @param pwd
	 * void
	 */
	void addAndUpdate(Org org);

	/**
	 * 删除组织机构 
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);

	/**
	 * 获取组织机构树 
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();
	
	/**
	 * 移动组织机构 
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param sourceId
	 * @param targetId
	 * @param saasId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 名称是否重复
	 * 
	 * v1.0 zhanghc 2020年8月24日下午1:53:35
	 * @param org
	 * @return boolean
	 */
	boolean existName(Org org);

	/**
	 * 编码是否重复
	 * 
	 * v1.0 zhanghc 2020年8月24日下午1:53:35
	 * @param org
	 * @return boolean
	 */
	boolean existCode(Org org);
}
