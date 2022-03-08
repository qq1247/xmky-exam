package com.wcpdoc.exam.core.dao;

import java.util.List;

import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 随机章节数据访问层接口
 * 
 * v1.0 chenyun 2022年2月11日 10:49:52
 */
public interface PaperQuestionRuleDao extends RBaseDao<PaperQuestionRule> {
	
	/**
	 * 获取随机章节列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午11:30:01
	 * @param paperId
	 * @param paperQuestionId
	 * @return List<PaperQuestionRule>
	 */
	List<PaperQuestionRule> getChapterList(Integer paperId, Integer paperQuestionId);
	
	/**
	 * 获取试题列表
	 * 
	 * v1.0 chenyun 2022年3月2日下午3:10:03
	 * @param questionTypeId
	 * @return List<Question>
	 */
	List<Question> getQuestionList(Integer questionTypeId);
}