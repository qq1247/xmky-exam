package com.wcpdoc.exam.sys.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.sys.entity.Res;

/**
 * 资源服务层接口
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
public interface ResService extends BaseService<Res> {

	/**
	 * 添加资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param res
	 * void
	 */
	void addAndUpdate(Res res);

	/**
	 * 修改资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param res
	 * void
	 */
	void editAndUpdate(Res res);

	/**
	 * 删除资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param ids
	 * void
	 */
	void delAndUpdate(Integer[] ids);

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
	 * 移动资源
	 * 
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * 
	 * @param sourceId
	 * @param targetId
	 * void
	 */
	void doMove(Integer sourceId, Integer targetId);

	/**
	 * 获取资源列表
	 * 
	 * v1.0 zhanghc 2016年8月20日下午4:50:40
	 * 
	 * @return List<Res>
	 */
	List<Res> getList();

}
