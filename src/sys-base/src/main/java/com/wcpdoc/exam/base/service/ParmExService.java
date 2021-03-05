package com.wcpdoc.exam.base.service;

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.core.service.BaseService;

/**
 * 参数服务层接口
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
public interface ParmExService extends BaseService<Parm> {
	
	/**
	 * 添加参数
	 * 
	 * v1.0 zhanghc 2021年3月5日上午11:27:20
	 * @param parm void
	 * @throws EmailException 
	 */
	void addAndUpdate(Parm parm);

	/**
	 * 修改参数
	 * 
	 * v1.0 zhanghc 2021年3月5日上午11:29:10
	 * @param parm 
	 * @throws EmailException 
	 */
	void updateAndUpdate(Parm parm);
}
