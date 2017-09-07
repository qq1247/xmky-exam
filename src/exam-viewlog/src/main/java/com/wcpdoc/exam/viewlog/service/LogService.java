package com.wcpdoc.exam.viewlog.service;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 日志服务层接口
 * 
 * v1.0 zhanghc 2017年4月20日下午11:53:05
 */
public interface LogService {

	/**
	 * 获取列表
	 * 
	 * v1.0 zhanghc 2017年4月20日下午11:53:05
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getListpage(PageIn pageIn);
}
