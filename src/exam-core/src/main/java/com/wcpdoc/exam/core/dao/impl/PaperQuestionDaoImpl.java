package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.PaperQuestionDao;
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
	public List<PaperQuestion> getChapterList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = :PAPER_ID AND TYPE = 1 ORDER BY NO ASC";
		return getList(sql, new Object[] { paperId }, PaperQuestion.class);
	}

	@Override
	public List<PaperQuestion> getChapterDetailList(Integer chapterId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PARENT_ID = :PARENT_ID ORDER BY NO ASC";
		return getList(sql, new Object[] { chapterId }, PaperQuestion.class);
	}

	@Override
	public PaperQuestion getEntity(Integer paperId, Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = :PAPER_ID AND QUESTION_ID = :QUESTION_ID";
		return getEntity(sql, new Object[]{paperId, questionId}, PaperQuestion.class);
	}
	
	@Override
	public PaperQuestion getEntity(Integer examId, Integer userId, Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID AND QUESTION_ID = :QUESTION_ID";
		return getEntity(sql, new Object[]{examId, userId, questionId}, PaperQuestion.class);
	}
	
	@Override
	public List<PaperQuestion> getList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = :PAPER_ID";
		return getList(sql, new Object[] { paperId });
	}
	
	@Override
	public List<PaperQuestion> getList(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE EXAM_ID = :EXAM_ID AND USER_ID = :USER_ID";
		return getList(sql, new Object[] { examId, userId });
	}

	@Override
	public List<PaperQuestion> getList2(Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE QUESTION_ID = :QUESTION_ID";
		return getList(sql, new Object[] { questionId }, PaperQuestion.class);
	}
}