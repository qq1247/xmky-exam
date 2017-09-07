package com.wcpdoc.exam.sys.service;

import com.wcpdoc.exam.sys.entity.Res;

/**
 * 资源扩展服务层接口
 * 
 * v1.0 zhanghc 2016-6-11下午8:57:40
 */
public interface ResExService{

	/**
	 * 删除关联引用
	 * v1.0 zhanghc 2016-6-11下午8:57:40
	 * @param org
	 * void
	 */
	void delAndUpdate(Res res);
	
}

