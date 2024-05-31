package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题扩展服务层接口
 * 
 * v1.0 zhanghc 2022年9月6日上午9:45:31
 */
public interface QuestionExService extends BaseService<Question> {

	/**
	 * 试题校验
	 * 
	 * v1.0 zhanghc 2022年9月6日上午9:49:47
	 * 
	 * @param question void
	 */
	void updateValid(Question question);

}
