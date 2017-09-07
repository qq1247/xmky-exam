package com.wcpdoc.exam.sys.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.sys.entity.Res;

/**
 * 资源数据访问层接口
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
public interface ResDao extends BaseDao<Res>{

	/**
	 * 获取资源树型列表
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * @param type
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getTreeList(Integer type);

	/**
	 * 移动资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * @param sourceId
	 * @param targetId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 获取最大权限位和最大权限码
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * @return Map<String,Object> {POS:1,CODE:2}
	 */
	Map<String, Object> getMaxAuthPosCode();

	/**
	 * 获取资源
	 * 
	 * v1.0 zhanghc 2016年7月15日上午9:57:54
	 * @param name
	 * @return
	 * Res
	 */
	Res getResByName(String name);

	/**
	 * 获取资源
	 * 
	 * v1.0 zhanghc 2016年7月15日上午9:58:14
	 * @param url
	 * @return
	 * Res
	 */
	Res getResByUrl(String url);

	/**
	 * 获取资源
	 * 
	 * v1.0 zhanghc 2016年8月13日上午11:09:30
	 * @param parentId
	 * @return List<Res>
	 */
	List<Res> getResByParentId(Integer parentId);

	/**
	 * 获取资源列表
	 * 
	 * v1.0 zhanghc 2016年8月20日下午4:51:45
	 * @return List<Res>
	 */
	List<Res> getList();
}
