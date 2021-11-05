package com.wcpdoc.base.service;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.service.BaseService;

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
	
	/**
	 * 图片转换icon格式
	 * 
	 * v1.0 chenyun 2021年10月8日下午2:36:39
	 * @param parm void
	 */
	void ImageIcon(Parm parm) throws Exception;
}
