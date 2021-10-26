package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 试题选项服务层接口
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
public interface QuestionOptionService extends BaseService<QuestionOption> {

	/**
	 * 删除试题选项
	 * 
	 * v1.0 chenyun 2021-03-10 16:11:06
	 * 
	 * @param id
	 * void
	 */
	void delAndUpdate(Integer id);
	
	/**
	 * 获取试题选项列表
	 * 
	 * v1.0 chenyun 2021年3月18日下午2:25:45
	 * @param questionId
	 * @return List<QuestionOption>
	 */
	List<QuestionOption> getList(Integer questionId);
	
}
