package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;

/**
 * 题库开放服务层接口
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
public interface QuestionTypeOpenService extends BaseService<QuestionTypeOpen> {
	
	/**
	 * 添加题库开放
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param id
	 * void
	 */
	void addEx(QuestionTypeOpen questionTypeOpen);
	
	/**
	 * 删除题库开放
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * 
	 * @param id
	 * void
	 */
	void delEx(Integer id);
	
	/**
	 * 试题开放列表
	 * 
	 * v1.0 chenyun 2021年9月29日上午10:50:30
	 * @return PageOut
	 */
	PageOut questionListpage(PageIn pageIn);
	
	/**
	 * 获取试题答案
	 * 
	 * v1.0 chenyun 2021年9月29日上午11:12:17
	 * @param questionId
	 * @return PageResultEx
	 */
	PageResultEx get(Integer questionId);
}
