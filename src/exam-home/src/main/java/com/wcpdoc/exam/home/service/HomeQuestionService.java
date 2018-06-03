package com.wcpdoc.exam.home.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.exam.core.service.BaseService;
/**
 * 试题服务层接口
 * 
 * v1.0 zhanghc 2018年6月3日上午10:34:21
 */
public interface HomeQuestionService extends BaseService<Object>{

	/**
	 * 获取试题分类树
	 * 
	 * v1.0 zhanghc 2018年6月3日上午11:40:54
	 * @param userId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getQuestionTypeTreeList(Integer userId);

}
