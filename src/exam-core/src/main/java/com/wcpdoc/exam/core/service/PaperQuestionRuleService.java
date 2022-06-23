package com.wcpdoc.exam.core.service;

import java.math.BigDecimal;
import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;

/**
 * 随机章节服务层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface PaperQuestionRuleService extends BaseService<PaperQuestionRule> {
	/**
	 * 更新规则
	 * 
	 * v1.0 chenyun 2022年3月9日下午5:30:00
	 * @param paperId
	 * @param chapterId
	 * @param questionTypeIds
	 * @param types
	 * @param difficultys
	 * @param ais
	 * @param aiOptions
	 * @param nums
	 * @param scores void
	 */
	void paperRuleUpdate(Integer paperId, Integer chapterId, Integer[] questionTypeIds, Integer[] types, String[] difficultys, String[] ais, String[] aiOptions, Integer[] nums, BigDecimal[] scores );
	
	/**
	 * 获取随机章节规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:30:01
	 * @param chapterId
	 * @return List<PaperQuestionRule>
	 */
	List<PaperQuestionRule> getList(Integer chapterId);
}
