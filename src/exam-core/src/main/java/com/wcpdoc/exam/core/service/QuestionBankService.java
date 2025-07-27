package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionBank;

/**
 * 题库服务层接口
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
public interface QuestionBankService extends BaseService<QuestionBank> {

	/**
	 * 题库删除
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @param id void
	 */
	void del(Integer id);

	/**
	 * 题库清空
	 * 
	 * v1.0 zhanghc 2022年9月15日上午9:28:44
	 * 
	 * @param id
	 * @return PageResult
	 */
	void clear(Integer id);

}
