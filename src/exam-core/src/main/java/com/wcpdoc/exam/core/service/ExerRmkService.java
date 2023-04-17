package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExerRmk;

/**
 * 模拟练习评论服务层接口
 * 
 * v1.0 chenyun 2021年8月31日上午9:54:12
 */
public interface ExerRmkService extends BaseService<ExerRmk> {

	/**
	 * 添加模拟练习评论
	 * 
	 * v1.0 chenyun 2021年8月31日下午1:55:07
	 * @param exerRmk
	 * @param anonymity void
	 */
	void addEx(ExerRmk exerRmk, Integer anonymity);

	/**
	 * 删除模拟练习评论 
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * @param id void
	 */
	void delEx(Integer id);
	
	/**
	 * 获取模拟练习评论列表
	 * 
	 * v1.0 chenyun 2021年8月31日上午10:18:42
	 * @param parentId
	 * @return List<ExerRmk>
	 */
	List<Map<String, Object>> getList(Integer parentId);
}
