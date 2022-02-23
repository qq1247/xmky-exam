package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.RandChapterRulesDao;
import com.wcpdoc.exam.core.entity.RandChapterRules;

/**
 * 随机章节数据访问层实现
 * 
 * v1.0 chenyun 2022年2月11日 10:51:12
 */
@Repository
public class RandChapterRulesDaoImpl extends RBaseDaoImpl<RandChapterRules> implements RandChapterRulesDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<RandChapterRules> getChapterList(Integer paperId, Integer paperQuestionId) {
		String sql = "SELECT *  FROM EXM_RAND_CHAPTER_RULES RAND_CHAPTER_RULES ";
		if (!ValidateUtil.isValid(paperQuestionId)) {
			sql += "WHERE RAND_CHAPTER_RULES.PAPER_ID = ? ORDER BY NO ASC ";
			return getList(sql, new Object[]{ paperId });
		}
		sql += "WHERE RAND_CHAPTER_RULES.PAPER_ID = ? AND RAND_CHAPTER_RULES.PAPER_QUESTION_ID = ? ORDER BY NO ASC ";
		return getList(sql, new Object[]{ paperId, paperQuestionId });
	}
}