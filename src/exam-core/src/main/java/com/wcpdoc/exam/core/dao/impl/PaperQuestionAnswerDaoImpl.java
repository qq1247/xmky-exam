package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.PaperQuestionAnswerDao;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;

/**
 * 试题答案数据访问层实现
 * 
 * v1.0 chenyun 2021-07-20 18:14:32
 */
@Repository
public class PaperQuestionAnswerDaoImpl extends RBaseDaoImpl<PaperQuestionAnswer> implements PaperQuestionAnswerDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<PaperQuestionAnswer> getList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION_ANSWER WHERE PAPER_ID = :PAPER_ID ORDER BY NO ASC";
		return getList(sql, new Object[] { paperId }, PaperQuestionAnswer.class);
	}

	@Override
	public List<PaperQuestionAnswer> getList(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION_ANSWER WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID ORDER BY NO ASC";
		return getList(sql, new Object[] { examId, userId }, PaperQuestionAnswer.class);
	}

	@Override
	public List<PaperQuestionAnswer> getListForSingleQuestion(Integer paperId, Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION_ANSWER WHERE PAPER_ID = :PAPER_ID AND QUESTION_ID = :QUESTION_ID ORDER BY NO ASC";
		return getList(sql, new Object[] { paperId, questionId }, PaperQuestionAnswer.class);
	}

	@Override
	public List<PaperQuestionAnswer> getListByChapter(Integer chapterId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION_ANSWER WHERE PAPER_QUESTION_ID = :PAPER_QUESTION_ID ORDER BY NO ASC";
		return getList(sql, new Object[] { chapterId }, PaperQuestionAnswer.class);
	}
}