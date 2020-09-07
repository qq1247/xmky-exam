package com.wcpdoc.exam.base.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.base.entity.Res;
import com.wcpdoc.exam.core.dao.RBaseDao;

/**
 * 资源数据访问层接口
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
public interface ResDao extends RBaseDao<Res> {

	/**
	 * 获取资源树
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param type
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList(Integer type);

	/**
	 * 获取资源列表
	 * 
	 * v1.0 zhanghc 2016年8月20日下午4:51:45
	 * 
	 * @return List<Res>
	 */
	List<Res> getList();

	/**
	 * 获取资源列表
	 * 
	 * v1.0 zhanghc 2020年8月23日下午6:10:06
	 * @param parentId
	 * @return List<Res>
	 */
	List<Res> getList(int parentId);

	/**
	 * 链接是否存在
	 * 
	 * v1.0 zhanghc 2020-06-10 12:40:00
	 * 
	 * @param name
	 * @param excludeId
	 * @return boolean
	 */
	boolean existName(String url, Integer excludeId);
	
	/**
	 * 获取最大权限位和最大权限码
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @return Map<String,Object> {POS:1, CODE:2}
	 */
	Map<String, Object> getMaxAuthPosCode();
}
