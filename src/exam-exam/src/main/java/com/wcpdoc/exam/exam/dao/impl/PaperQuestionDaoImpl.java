package com.wcpdoc.exam.exam.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.dao.PaperQuestionDao;
import com.wcpdoc.exam.exam.entity.PaperQuestion;

/**
 * 试卷试题数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Repository
public class PaperQuestionDaoImpl extends BaseDaoImpl<PaperQuestion> implements PaperQuestionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public PaperQuestion getPaperQuestionByName(String name) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE NAME = ?";
		return getUnique(sql, new Object[]{name}, PaperQuestion.class);
	}

	@Override
	public List<PaperQuestion> getList(Integer parentId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PARENT_ID = ?";
		return getList(sql, new Object[]{parentId}, PaperQuestion.class);
	}

	@Override
	public PaperQuestion getTopPaperQuestion(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ? AND PARENT_ID = 0";
		return getUnique(sql, new Object[]{paperId}, PaperQuestion.class);
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ?";
		return getList(sql, new Object[]{paperId}, PaperQuestion.class);
	}

	@Override
	public PaperQuestion getEntity(Integer paperId, Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ? AND QUESTION_ID = ?";
		return getUnique(sql, new Object[]{paperId, questionId}, PaperQuestion.class);
	}
}