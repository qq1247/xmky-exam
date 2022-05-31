package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.PaperQuestionRuleDao;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;

/**
 * 随机章节数据访问层实现
 * 
 * v1.0 chenyun 2022年2月11日 10:51:12
 */
@Repository
public class PaperQuestionRuleDaoImpl extends RBaseDaoImpl<PaperQuestionRule> implements PaperQuestionRuleDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}
	
	@Override
	public List<PaperQuestionRule> getList(Integer chapterId) {
		String sql = "SELECT * "
				+ "FROM EXM_PAPER_QUESTION_RULE "
				+ "WHERE PAPER_QUESTION_ID = :PAPER_QUESTION_ID "
				+ "ORDER BY NO ASC ";
		return getList(sql, new Object[]{ chapterId });
	}
}