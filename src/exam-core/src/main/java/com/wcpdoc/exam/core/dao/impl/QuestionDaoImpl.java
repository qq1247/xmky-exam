package com.wcpdoc.exam.core.dao.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试题数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Repository
public class QuestionDaoImpl extends RBaseDaoImpl<Question> implements QuestionDao {
	@Resource
	private UserDao userDao;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION.ID, QUESTION.TYPE, QUESTION.DIFFICULTY, QUESTION.TITLE, "
				+ "QUESTION.STATE, QUESTION.AI, QUESTION.QUESTION_TYPE_ID, QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, "
				+ "QUESTION.SCORE, QUESTION.SCORE_OPTIONS, QUESTION.ANALYSIS, CREATE_USER.NAME AS CREATE_USER_NAME "
				+ "FROM EXM_QUESTION QUESTION "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID "
				+ "LEFT JOIN SYS_USER CREATE_USER ON QUESTION.CREATE_USER_ID = CREATE_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeId")), "QUESTION.QUESTION_TYPE_ID = ?", pageIn.get("questionTypeId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeName")), "QUESTION_TYPE.NAME LIKE ?", String.format("%%%s%%", pageIn.get("questionTypeName")))
				.addWhere(ValidateUtil.isValid(pageIn.get("id")), "QUESTION.ID = ?", pageIn.get("id"))
				.addWhere(ValidateUtil.isValid(pageIn.get("title")), "QUESTION.TITLE LIKE ?", String.format("%%%s%%", pageIn.get("title")))
				.addWhere(ValidateUtil.isValid(pageIn.get("type")), "QUESTION.TYPE = ?", pageIn.get("type"))
				.addWhere(ValidateUtil.isValid(pageIn.get("difficulty")), "QUESTION.DIFFICULTY = ?", pageIn.get("difficulty"))
				.addWhere(ValidateUtil.isValid(pageIn.get("score")), "QUESTION.SCORE = ?", pageIn.get("score"))
				.addWhere(ValidateUtil.isValid(pageIn.get("ai")), "QUESTION.AI = ?", pageIn.get("ai"))
				.addWhere(ValidateUtil.isValid(pageIn.get("paperId", Integer.class)), "EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = ? AND Z.QUESTION_ID = QUESTION.ID)", pageIn.get("paperId", Integer.class))
                .addWhere(ValidateUtil.isValid(pageIn.get("exPaperId")), "NOT EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = ? AND Z.QUESTION_ID = QUESTION.ID)", pageIn.get("exPaperId"))
				.addWhere(!ValidateUtil.isValid(pageIn.get("state")), "QUESTION.STATE IN (1,2)")// 默认查询发布和草稿状态
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && "0".equals(pageIn.get("state")), "QUESTION.STATE = 0 AND QUESTION.UPDATE_TIME > ?", DateUtil.getNextDay(new Date(), -7))// 查询已删除并且最近7天的试题（回收站使用）
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "QUESTION.STATE = ?", pageIn.get("state"))// 默认查询发布和草稿状态
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "QUESTION_TYPE.WRITE_USER_IDS LIKE ?", String.format("%%%s%%", pageIn.get("curUserId", Integer.class)))// 只看自己的
				.addOrder(!ValidateUtil.isValid(pageIn.get("rand")), "QUESTION.UPDATE_TIME", Order.DESC)
				.addOrder(ValidateUtil.isValid(pageIn.get("rand")), "Rand()", Order.NULL);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_QUESTION WHERE STATE IN (1, 2) AND QUESTION_TYPE_ID = ?";
		return getList(sql, new Object[] { questionTypeId });
	}

	@Override
	public List<Question> randomQuestion(Integer questionTypeId, Integer type, Integer difficulty,  BigDecimal queryScore, Integer ai, Integer totalNumber) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE QUESTION.STATE = 1 AND QUESTION.QUESTION_TYPE_ID = ? AND QUESTION.TYPE = ? ";
		
		if (ValidateUtil.isValid(difficulty) && ValidateUtil.isValid(queryScore) && ValidateUtil.isValid(ai) ) {
			sql += "AND QUESTION.DIFFICULTY = ? AND QUESTION.SCORE = ? AND QUESTION.AI = ? ORDER BY RAND() LIMIT 0,? ";
			return getList(sql, new Object[] { questionTypeId, type, difficulty, queryScore, ai, totalNumber}, Question.class);
		}
		
		if (!ValidateUtil.isValid(difficulty) && !ValidateUtil.isValid(queryScore) && ValidateUtil.isValid(ai) ) {
			sql += "AND QUESTION.AI = ? ORDER BY RAND() LIMIT 0,? ";
			return getList(sql, new Object[] { questionTypeId, type, ai, totalNumber}, Question.class);
		}
		
		if (!ValidateUtil.isValid(difficulty) && ValidateUtil.isValid(queryScore) && ValidateUtil.isValid(ai) ) {
			sql += "AND QUESTION.SCORE = ? AND QUESTION.AI = ? ORDER BY RAND() LIMIT 0,? ";
			return getList(sql, new Object[] { questionTypeId, type, queryScore, ai, totalNumber}, Question.class);
		}
		
		if (!ValidateUtil.isValid(difficulty) && ValidateUtil.isValid(queryScore) && !ValidateUtil.isValid(ai) ) {
			sql += "AND QUESTION.SCORE = ? ORDER BY RAND() LIMIT 0,? ";
			return getList(sql, new Object[] { questionTypeId, type, queryScore, totalNumber}, Question.class);
		}
		
		if (ValidateUtil.isValid(difficulty) && !ValidateUtil.isValid(queryScore) && ValidateUtil.isValid(ai) ) {
			sql += "AND QUESTION.DIFFICULTY = ? AND QUESTION.AI = ? ORDER BY RAND() LIMIT 0,? ";
			return getList(sql, new Object[] { questionTypeId, type, difficulty, ai, totalNumber}, Question.class);
		}

		if (ValidateUtil.isValid(difficulty) && ValidateUtil.isValid(queryScore) && !ValidateUtil.isValid(ai) ) {
			sql += "AND QUESTION.DIFFICULTY = ? ORDER BY RAND() LIMIT 0,? ";
			return getList(sql, new Object[] { questionTypeId, type, difficulty, totalNumber}, Question.class);
		}
		
		if (ValidateUtil.isValid(difficulty) && !ValidateUtil.isValid(queryScore) && !ValidateUtil.isValid(ai) ) {
			sql += "AND QUESTION.DIFFICULTY = ? ORDER BY RAND() LIMIT 0,? ";
			return getList(sql, new Object[] { questionTypeId, type, difficulty, totalNumber}, Question.class);
		}
		
		sql += "ORDER BY RAND() LIMIT 0,? ";
		return getList(sql, new Object[] { questionTypeId, type, totalNumber}, Question.class);
	}

	@Override
	public PageOut getListpageMap(PageIn pageIn) {
		String sql = "SELECT QUESTION.ID, QUESTION.TYPE, QUESTION.DIFFICULTY, QUESTION.AI, QUESTION.SCORE "
				+ "FROM EXM_QUESTION QUESTION ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeId", Integer.class)), "QUESTION.QUESTION_TYPE_ID = ?", pageIn.get("questionTypeId", Integer.class))
			   .addWhere("QUESTION.STATE = 1 ");
		return getListpage(sqlUtil, pageIn);
	}
}