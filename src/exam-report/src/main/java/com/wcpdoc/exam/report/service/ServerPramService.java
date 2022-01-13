package com.wcpdoc.exam.report.service;

import java.util.List;
import java.util.Map;

/**
 * 服务器参数服务层接口
 * 
 * v1.0 zhanghc 2021年12月14日上午11:15:01
 */
public interface ServerPramService {
	
	/**
	 * 获取参数列表
	 * 
	 * v1.0 zhanghc 2021年12月14日下午5:29:03
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getList();
}
