package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Exer;

/**
 * 模拟练习服务层接口
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
public interface ExerService extends BaseService<Exer> {

	/**
	 * 试题模拟添加
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param id
	 * void
	 */
	void addEx(Exer sim);

}
