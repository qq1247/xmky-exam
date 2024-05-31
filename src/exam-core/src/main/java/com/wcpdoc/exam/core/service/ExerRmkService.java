package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.ExerRmk;

/**
 * 练习评论服务层接口
 * 
 * v1.0 chenyun 2021年8月31日上午9:54:12
 */
public interface ExerRmkService extends BaseService<ExerRmk> {
	/**
	 * 评论
	 * 
	 * v1.0 zhanghc 2023年5月5日上午1:17:30
	 * 
	 * @param exerRmk 评论
	 * @param anon    是否匿名（true：是；false：否） void
	 */
	void rmk(ExerRmk exerRmk, Boolean anon);

	/**
	 * 点赞
	 * 
	 * v1.0 zhanghc 2023年4月17日下午7:53:32
	 * 
	 * @param id void
	 */
	void like(Integer id);
}
