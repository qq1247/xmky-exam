package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.PaperQuestionDao;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;

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
	public List<PaperQuestion> getQuestionList(Integer parentId, Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION ";
		if (examId != null) {
			sql += "WHERE PARENT_ID = ? AND EXAM_ID = ? AND USER_ID = ? AND TYPE != 1 ORDER BY NO ASC";
			return getList(sql, new Object[]{parentId, examId, userId}, PaperQuestion.class);
		}
		sql += "WHERE PARENT_ID = ? AND TYPE != 1 ORDER BY NO ASC";
		return getList(sql, new Object[]{parentId}, PaperQuestion.class);
	}

	@Override
	public List<PaperQuestion> getList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ?";
		return getList(sql, new Object[]{paperId}, PaperQuestion.class);
	}
	
	@Override
	public List<PaperQuestion> getChapterList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ? AND TYPE = 1 ORDER BY NO ASC";
		return getList(sql, new Object[]{paperId}, PaperQuestion.class);
	}

	@Override
	public PaperQuestion getEntity(Integer paperId, Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ? AND QUESTION_ID = ?";
		return getEntity(sql, new Object[]{paperId, questionId}, PaperQuestion.class);
	}
	
	@Override
	public PaperQuestion getEntity(Integer examId, Integer paperId, Integer questionId, Integer userId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE EXAM_ID = ? AND PAPER_ID = ? AND QUESTION_ID = ? AND USER_ID = ?";
		return getEntity(sql, new Object[]{examId, paperId, questionId, userId}, PaperQuestion.class);
	}
	
	@Override
	public void del(Integer examId, Integer userId) {
		String sql = "DELETE FROM EXM_PAPER_QUESTION WHERE TYPE != 1 AND EXAM_ID = ? AND USER_ID = ? ";
		update(sql, new Object[] { examId, userId });
	}

	@Override
	public List<Question> getQuestionRandList(Integer examId, Integer paperId) {
		String sql = "SELECT QUESTION.* " //ID, QUESTION.TYPE, QUESTION.DIFFICULTY, QUESTION.TITLE, QUESTION.ANALYSIS, QUESTION.AI, QUESTION.AI_OPTIONS, QUESTION.SCORE
				+ "FROM EXM_PAPER_QUESTION PAPER_QUESTION "
				+ "INNER JOIN EXM_QUESTION QUESTION ON PAPER_QUESTION.QUESTION_ID = QUESTION.ID "
				+ "WHERE PAPER_QUESTION.TYPE != 1 AND PAPER_QUESTION.EXAM_ID = ? AND PAPER_QUESTION.PAPER_ID = ? ";
		return getList(sql, new Object[]{ examId, paperId }, Question.class);
	}
	
	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer examId, Integer paperId) {
		String sql = "SELECT PAPER_QUESTION.* "
				+ "FROM EXM_PAPER_QUESTION PAPER_QUESTION "
				+ "WHERE PAPER_QUESTION.TYPE != 1 AND PAPER_QUESTION.EXAM_ID = ? AND PAPER_QUESTION.PAPER_ID = ? ";
		return getList(sql, new Object[]{ examId, paperId }, PaperQuestion.class);
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer questionId) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE QUESTION_ID = ?";
		return getList(sql, new Object[]{ questionId }, PaperQuestion.class);
	}
}