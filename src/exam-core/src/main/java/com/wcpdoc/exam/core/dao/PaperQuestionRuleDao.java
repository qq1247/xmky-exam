package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;

/**
 * 随机章节数据访问层接口
 * 
 * v1.0 chenyun 2022年2月11日 10:49:52
 */
public interface PaperQuestionRuleDao extends RBaseDao<PaperQuestionRule> {
	
	/**
	 * 获取随机章节规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:30:01
	 * @param chapterId
	 * @return List<PaperQuestionRule>
	 */
	List<PaperQuestionRule> getList(Integer chapterId);
}