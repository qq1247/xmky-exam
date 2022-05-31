package com.wcpdoc.base.dao;

import java.util.List;

import com.wcpdoc.base.entity.Org;
import com.wcpdoc.core.dao.RBaseDao;

/**
 * 机构数据访问层接口
 * 
 * v1.0 zhanghc 2016-5-8上午11:00:00
 */
public interface OrgDao extends RBaseDao<Org> {

	/**
	 * 获取机构 列表
	 * 
	 * v1.0 chenyun 2020-6-4 08:59:46
	 * 
	 * @return List<Dict>
	 */
	List<Org> getList();
	
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
	 * 名称是否存在
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	boolean existName(String name, Integer excludeId);

	/**
	 * 编码是否存在
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	boolean existCode(String code, Integer excludeId);
	
	/**
	 * 获取机构
	 * 
	 * v1.0 chenyun 2021年3月5日上午10:41:51
	 * @param name
	 * @return Org
	 */
	Org getOrg(String name);
}
