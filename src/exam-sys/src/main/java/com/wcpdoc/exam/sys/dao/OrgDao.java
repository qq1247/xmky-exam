package com.wcpdoc.exam.sys.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.sys.entity.Org;

/**
 * 组织机构数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgDao extends BaseDao<Org>{

	/**
	 * 获取组织机构树型列表
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList();

	/**
	 * 移动组织机构
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * @param sourceId
	 * @param targetId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 获取所有子组织机构列表，包括自己
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * @param id 组织机构id
	 * void
	 */
	List<Org> getAllSubOrgList(Integer id);

	/**
	 * 获取组织机构
	 * v1.0 zhanghc 2016年7月9日下午11:42:15
	 * @param name
	 * @return Org
	 */
	Org getOrgByName(String name);
}
