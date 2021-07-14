package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.PaperQuestionDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PaperQuestion;

/**
 * 试卷试题数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-26 14:23:38
 */
@Repository
public class PaperQuestionDaoImpl extends RBaseDaoImpl<PaperQuestion> implements PaperQuestionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<PaperQuestion> getQuestionList(Integer parentId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PARENT_ID = ? AND TYPE != 1";
		return getList(sql, new Object[]{parentId}, PaperQuestion.class);
	}

	@Override
	public List<PaperQuestion> getList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ?";
		return getList(sql, new Object[]{paperId}, PaperQuestion.class);
	}
	
	@Override
	public List<PaperQuestion> getChapterList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ? AND TYPE = 1";
		return getList(sql, new Object[]{paperId}, PaperQuestion.class);
	}

	@Override
	public PaperQuestion getEntity(Integer paperId, Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ? AND QUESTION_ID = ?";
		return getEntity(sql, new Object[]{paperId, questionId}, PaperQuestion.class);
	}

	@Override
	public PaperQuestion getEntityByChapter(Integer parentId, Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PARENT_ID = ? AND QUESTION_ID = ?";
		return getEntity(sql, new Object[]{parentId, questionId}, PaperQuestion.class);
	}
}