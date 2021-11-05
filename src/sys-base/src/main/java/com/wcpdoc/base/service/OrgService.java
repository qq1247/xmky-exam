package com.wcpdoc.base.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.core.service.BaseService;

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
	
	/**
	 * 获取组织机构列表
	 * 
	 * v1.0 chenyun 2021年3月5日上午10:41:51
	 * @return Org
	 */
	Org getOrg(String name);
	
	/**
	 * 获取组织机构列表
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	List<Org> getList(Integer parentId);
	
	/**
	 * 获取住址机构
	 * 
	 * v1.0 chenyun 2021年3月8日下午1:24:31
	 * @param id
	 * @return Map<String,Object>
	 */
	Map<String, Object> getOrg(Integer id);
	
	/**
	 * 同步组织机构
	 * 
	 * v1.0 chenyun 2021年3月26日下午3:49:33
	 * @param orgName
	 * @param orgCode
	 * @return Integer
	 */
	Integer syncOrg(String orgName, String orgCode);
}
