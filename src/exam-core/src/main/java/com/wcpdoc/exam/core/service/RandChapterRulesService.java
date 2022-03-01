package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.RandChapterRules;


/**
 * 随机章节服务层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface RandChapterRulesService extends BaseService<RandChapterRules> {
	/**
	 * 添加随机章节规则
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:01:19
	 * @param randChapterRules void
	 */
	void addAndUpdate(RandChapterRules randChapterRules);
	
	/**
	 * 修改随机章节规则
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:01:19
	 * @param randChapterRules void
	 */
	void updateAndUpdate(RandChapterRules randChapterRules);
	
	/**
	 * 删除随机章节规则
	 * 
	 * v1.0 chenyun 2022年2月11日下午3:55:33
	 * @param ids void
	 */
	void delAndUpdate(Integer[] ids);
	
	/**
	 * 获取随机章节规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日下午2:38:31
	 * @param paperId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> randChapterRulesList(Integer paperId);
	
	/**
	 * 获取随机章节规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:30:01
	 * @param paperId
	 * @param paperQuestionId
	 * @return List<RandChapterRules>
	 */
	List<RandChapterRules> getChapterList(Integer paperId, Integer paperQuestionId);
	
	/**
	 * 章节随机规则中所有试题分类id
	 * 
	 * v1.0 chenyun 2022年2月18日上午9:27:27
	 * @param paperId
	 * @return Map<Integer, List<Map<String, Object>>>
	 */
	Map<Integer, List<Map<String, Object>>> checkRandChapterRules(Integer paperId);
}
