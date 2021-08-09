package com.wcpdoc.exam.base.service;

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.core.service.BaseService;

/**
 * 参数服务层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface ParmService extends BaseService<Parm> {
	/**
	 * 添加参数
	 * 
	 * v1.0 zhanghc 2021年3月5日上午11:22:59
	 * @param parm void
	 */
	void addAndUpdate(Parm parm);

	/**
	 * 更新参数
	 * 
	 * v1.0 zhanghc 2021年3月5日上午11:21:35
	 * @param parm void
	 */
	void updateAndUpdate(Parm parm);
}
