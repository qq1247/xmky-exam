package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperDao;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 试卷数据访问层实现
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
@Repository
public class PaperDaoImpl extends RBaseDaoImpl<Paper> implements PaperDao {
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT PAPER.ID, PAPER.NAME, PAPER.STATE, "
				+ "PAPER.PASS_SCORE, PAPER.TOTAL_SCORE, PAPER.PAPER_TYPE_ID, PAPER_TYPE.NAME AS PAPER_TYPE_NAME, "
				+ "PAPER.MARK_TYPE, PAPER.SHOW_TYPE, PAPER.GEN_TYPE "
				+ "FROM EXM_PAPER PAPER "
				+ "LEFT JOIN EXM_PAPER_TYPE PAPER_TYPE ON PAPER.PAPER_TYPE_ID = PAPER_TYPE.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("paperTypeId")), "PAPER.PAPER_TYPE_ID = :PAPER_TYPE_ID", pageIn.get("paperTypeId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("name")), "PAPER.NAME LIKE :NAME", "%" + pageIn.get("name") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "PAPER_TYPE.READ_USER_IDS LIKE :READ_USER_IDS", String.format("%%,%s,%%", pageIn.get("curUserId", Integer.class)))// 只看自己的
				.addWhere(!ValidateUtil.isValid(pageIn.get("state")), "PAPER.STATE IN (1,2)")
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && "0".equals(pageIn.get("state")), "PAPER.STATE IN (1,2)")
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "PAPER.STATE = :STATE", pageIn.get("state"))
				.addOrder("PAPER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Question> getQuestionList(Integer id) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = :PAPER_ID AND Z.TYPE = 2 AND Z.QUESTION_ID = QUESTION.ID)";// AND QUESTION.STATE = 1 如果删除试题，试卷关联的试题也能看
		return getList(sql, new Object[] { id }, Question.class);
	}
	
	@Override
	public List<Question> getQuestionList(Integer id, Integer examId) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.EXAM_ID = :EXAM_ID AND Z.PAPER_ID = :PAPER_ID AND Z.TYPE = 3 AND Z.QUESTION_ID = QUESTION.ID)";// AND QUESTION.STATE = 1 如果删除试题，试卷关联的试题也能看
		return getList(sql, new Object[] { examId, id }, Question.class);
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer id) {
		String sql = "SELECT PAPER_QUESTION.* "
				+ "FROM EXM_PAPER_QUESTION PAPER_QUESTION "
				+ "WHERE PAPER_QUESTION.PAPER_ID = :PAPER_ID AND PAPER_QUESTION.TYPE = 2";
		return getList(sql, new Object[]{id}, PaperQuestion.class);
	}
	
	@Override
	public List<Paper> getList(Integer paperTypeId) {
		String sql = "SELECT * FROM EXM_PAPER WHERE STATE != 0 AND PAPER_TYPE_ID = :PAPER_TYPE_ID";
		return getList(sql, new Object[] { paperTypeId }, Paper.class);
	}
}