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
	 * 机构添加
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @param phone
	 * @param pwd   void
	 */
	void addEx(Org org);

	/**
	 * 机构修改
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param org
	 * @param phone
	 * @param pwd   void
	 */
	void editEx(Org org);

	/**
	 * 机构删除
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param id void
	 */
	void delEx(Integer id);

	/**
	 * 机构移动
	 * 
	 * v1.0 zhanghc 2016-5-8上午11:00:00
	 * 
	 * @param sourceId
	 * @param targetId
	 * @param saasId   void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 机构列表
	 * 
	 * v1.0 zhanghc 2022年5月10日上午11:32:07
	 * 
	 * @return List<Org>
	 */
	List<Org> getList();
}
