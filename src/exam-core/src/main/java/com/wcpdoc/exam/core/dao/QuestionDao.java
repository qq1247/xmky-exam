package com.wcpdoc.exam.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题数据访问层接口
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
public interface QuestionDao extends BaseDao<Question>{

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2021年12月28日下午2:56:17
	 * @param questionTypeId
	 * @return List<Question>
	 */
	List<Question> getList(Integer questionTypeId);

	
	/**
	 * 获取随机试题id
	 * 
	 * v1.0 chenyun 2022年2月11日下午1:35:07
	 * @return List<Integer>
	 */
	List<Question> randomQuestion(Integer questionTypeId, Integer type, Integer difficulty, BigDecimal queryScore, Integer ai, Integer totalNumber);
	
	/**
	 * 试题分类下试题 (章节随机规则)
	 * 
	 * v1.0 chenyun 2022年2月18日上午11:14:03
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getListpageMap(PageIn pageIn);
}
