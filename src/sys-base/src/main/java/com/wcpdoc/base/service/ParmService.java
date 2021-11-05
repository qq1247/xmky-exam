package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.service.BaseService;

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
	
	/**
	 * 获取实体
	 * 
	 * v1.0 chenyun 2021年9月14日上午11:26:30
	 * @param userId
	 * @return Parm
	 */
	Parm get();
	
	/**
	 * 修改名称和logo
	 * 
	 * v1.0 chenyun 2021年10月8日下午2:30:00
	 * @param parm void
	 */
	void editLogo(Parm parm) throws Exception;
}
