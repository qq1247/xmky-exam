package com.wcpdoc.base.service;

import java.util.List;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.core.service.BaseService;

/**
 * 机构服务层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgService extends BaseService<Org> {

	/**
	 * 添加机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @param phone
	 * @param pwd   void
	 */
	void addEx(Org org);

	/**
	 * 删除机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id void
	 */
	void delEx(Integer id);

	/**
	 * 移动机构
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param sourceId
	 * @param targetId
	 * @param saasId   void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 名称是否重复
	 * 
	 * v1.0 zhanghc 2020年8月24日下午1:53:35
	 * 
	 * @param org
	 * @return boolean
	 */
	boolean existName(Org org);

	/**
	 * 编码是否重复
	 * 
	 * v1.0 zhanghc 2020年8月24日下午1:53:35
	 * 
	 * @param org
	 * @return boolean
	 */
	boolean existCode(Org org);

	/**
	 * 获取机构列表
	 * 
	 * v1.0 chenyun 2021年3月5日上午10:41:51
	 * 
	 * @return Org
	 */
	Org getOrg(String name);

	/**
	 * 获取机构列表
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	List<Org> getList(Integer parentId);

	/**
	 * 获取机构列表
	 * 
	 * v1.0 zhanghc 2022年5月10日上午11:32:07
	 * 
	 * @return List<Org>
	 */
	List<Org> getList();
}
