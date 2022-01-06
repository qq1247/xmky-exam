package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
/**
 * 试题答案服务层接口
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
public interface QuestionAnswerService extends BaseService<QuestionAnswer>{
	
	/**
	 * 查询试题答案列表
	 * 
	 * v1.0 chenyun 2021年7月20日上午10:29:11
	 * @param questionId
	 * @return List<QuestionAnswer>
	 */
	List<QuestionAnswer> getList(Integer questionId);
	
	/**
	 * 删除试题答案
	 * 
	 * v1.0 chenyun 2021年7月21日下午7:08:30
	 * @param id void
	 */
	void updateAndDel(Integer id);
}
