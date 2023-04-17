package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.ExerRmk;

/**
 * 模拟练习评论
 * 
 * v1.0 chenyun 2021年8月31日上午9:59:36
 */
public interface ExerRmkDao extends BaseDao<ExerRmk>{

	/**
	 * 获取模拟练习评论列表
	 * 
	 * v1.0 chenyun 2021年8月31日上午10:18:42
	 * @param parentId
	 * @return List<ExerRmk>
	 */
	List<Map<String, Object>> getList(Integer parentId);
}
