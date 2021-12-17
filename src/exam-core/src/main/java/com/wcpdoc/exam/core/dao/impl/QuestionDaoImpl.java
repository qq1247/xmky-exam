package com.wcpdoc.exam.core.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
				+ "QUESTION.STATE, QUESTION.QUESTION_TYPE_ID, QUESTION.SCORE, QUESTION.SCORE_OPTIONS, "
				+ "QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, UPDATE_USER.NAME AS UPDATE_USER_NAME, "
				+ "CREATE_USER.NAME AS CREATE_USER_NAME "
				+ "FROM EXM_QUESTION QUESTION "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID "
				+ "LEFT JOIN SYS_USER CREATE_USER ON QUESTION.CREATE_USER_ID = CREATE_USER.ID "
				+ "LEFT JOIN SYS_USER UPDATE_USER ON QUESTION.UPDATE_USER_ID = UPDATE_USER.ID ";
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
	public List<Map<String, Object>> accuracy(Integer examId) {
		String sql = "SELECT COUNT(ID) AS TOTAL, SUM( SCORE = QUESTION_SCORE ) AS CORRECT, QUESTION_ID FROM EXM_MY_EXAM_DETAIL WHERE EXAM_ID = ? GROUP BY QUESTION_ID ";
		return getMapList(sql, new Object[] { examId });
	}

	@Override
	public void updateQuestionType(Integer sourceId, Integer targetId) {
		String sql = "UPDATE EXM_QUESTION SET QUESTION_TYPE_ID = ? WHER QUESTION_TYPE_ID = ?";
		update(sql, new Object[]{targetId, sourceId});
	}

	@Override
	public void publish(Integer questionTypeId) {
		String sql = "UPDATE EXM_QUESTION SET STATE = 1 WHERE STATE = 2 AND QUESTION_TYPE_ID = ?";
		update(sql, new Object[]{ questionTypeId });
	}
}