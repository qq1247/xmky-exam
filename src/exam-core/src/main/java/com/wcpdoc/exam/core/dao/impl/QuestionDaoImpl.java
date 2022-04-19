package com.wcpdoc.exam.core.dao.impl;

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
				+ "QUESTION.SCORE, QUESTION.AI_OPTIONS, QUESTION.ANALYSIS, CREATE_USER.NAME AS CREATE_USER_NAME "
				+ "FROM EXM_QUESTION QUESTION "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID "
				+ "LEFT JOIN SYS_USER CREATE_USER ON QUESTION.CREATE_USER_ID = CREATE_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeId")), "QUESTION.QUESTION_TYPE_ID = :QUESTION.QUESTION_TYPE_ID", pageIn.get("questionTypeId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeName")), "QUESTION_TYPE.NAME LIKE :QUESTION_TYPE.NAME", String.format("%%%s%%", pageIn.get("questionTypeName")))
				.addWhere(ValidateUtil.isValid(pageIn.get("id")), "QUESTION.ID = :QUESTION.ID", pageIn.get("id"))
				.addWhere(ValidateUtil.isValid(pageIn.get("title")), "QUESTION.TITLE LIKE :QUESTION.TITLE", String.format("%%%s%%", pageIn.get("title")))
				.addWhere(ValidateUtil.isValid(pageIn.get("type")), "QUESTION.TYPE = :QUESTION.TYPE", pageIn.get("type"))
				.addWhere(ValidateUtil.isValid(pageIn.get("difficulty")), "QUESTION.DIFFICULTY = :QUESTION.DIFFICULTY", pageIn.get("difficulty"))
				.addWhere(ValidateUtil.isValid(pageIn.get("score")), "QUESTION.SCORE = :QUESTION.SCORE", pageIn.get("score"))
				.addWhere(ValidateUtil.isValid(pageIn.get("ai")), "QUESTION.AI = :QUESTION.AI", pageIn.get("ai"))
				.addWhere(ValidateUtil.isValid(pageIn.get("paperId", Integer.class)), "EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = :Z.PAPER_ID AND Z.QUESTION_ID = QUESTION.ID)", pageIn.get("paperId", Integer.class))
                .addWhere(ValidateUtil.isValid(pageIn.get("exPaperId")), "NOT EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = :Z.PAPER_ID AND Z.QUESTION_ID = QUESTION.ID)", pageIn.get("exPaperId"))
				.addWhere(!ValidateUtil.isValid(pageIn.get("state")), "QUESTION.STATE IN (1,2)")// 默认查询发布和草稿状态
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && "0".equals(pageIn.get("state")), "QUESTION.STATE = 0 AND QUESTION.UPDATE_TIME > :QUESTION.UPDATE_TIME", DateUtil.getNextDay(new Date(), -7))// 查询已删除并且最近7天的试题（回收站使用）
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "QUESTION.STATE = :QUESTION.STATE", pageIn.get("state"))// 默认查询发布和草稿状态
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "QUESTION_TYPE.WRITE_USER_IDS LIKE :QUESTION_TYPE.WRITE_USER_IDS", String.format("%%%s%%", pageIn.get("curUserId", Integer.class)))// 只看自己的
				.addOrder(!ValidateUtil.isValid(pageIn.get("rand")), "QUESTION.UPDATE_TIME", Order.DESC)
				.addOrder(ValidateUtil.isValid(pageIn.get("rand")), "Rand()", Order.NULL);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_QUESTION WHERE STATE IN (1, 2) AND QUESTION_TYPE_ID = :QUESTION_TYPE_ID";
		return getList(sql, new Object[] { questionTypeId });
	}
}