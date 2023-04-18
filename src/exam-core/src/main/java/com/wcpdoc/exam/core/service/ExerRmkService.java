package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExerRmk;

/**
 * 模拟练习评论服务层接口
 * 
 * v1.0 chenyun 2021年8月31日上午9:54:12
 */
public interface ExerRmkService extends BaseService<ExerRmk> {

	/**
	 * 模拟练习评论添加
	 * 
	 * v1.0 chenyun 2021年8月31日下午1:55:07
	 * @param exerRmk
	 * @param anon 是否匿名（1：是；2：否） 
	 * void
	 */
	void addEx(ExerRmk exerRmk, Integer anon);

	/**
	 * 模拟练习评论点赞
	 * 
	 * v1.0 zhanghc 2023年4月17日下午7:53:32
	 * @param id void
	 */
	void like(Integer id);
}
