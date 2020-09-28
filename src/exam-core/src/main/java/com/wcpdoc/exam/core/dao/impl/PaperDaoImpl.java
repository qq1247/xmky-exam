package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.dao.PaperDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试卷数据访问层实现
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
@Repository
public class PaperDaoImpl extends RBaseDaoImpl<Paper> implements PaperDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT PAPER.ID, PAPER.NAME, PAPER_TYPE.NAME AS PAPER_TYPE_NAME, PAPER.STATE, PAPER.TOTAL_SCORE "
				+ "FROM EXM_PAPER PAPER "
				+ "LEFT JOIN EXM_PAPER_TYPE PAPER_TYPE ON PAPER.PAPER_TYPE_ID = PAPER_TYPE.ID ";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "PAPER.PAPER_TYPE_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "PAPER.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "PAPER.STATE = ?", pageIn.getThree())
				.addWhere(ValidateUtil.isValid(pageIn.getEight()), 
						"(PAPER_TYPE.USER_IDS LIKE ? "
								+ "OR EXISTS (SELECT 1 FROM SYS_USER Z WHERE Z.ID = ? AND PAPER_TYPE.ORG_IDS LIKE CONCAT('%,', Z.ORG_ID, ',%')) "
								+ "OR EXISTS (SELECT 1 FROM SYS_POST_USER Z WHERE Z.USER_ID = ? AND PAPER_TYPE.POST_IDS LIKE CONCAT('%,', Z.POST_ID, ',%')))", 
						"%," + pageIn.getEight() + ",%", pageIn.getEight(), pageIn.getEight())
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "PAPER.STATE = ?", pageIn.getTen())
				.addWhere("PAPER.STATE != ?", 0)
				.addOrder("PAPER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "STATE", "STATE");
		return pageOut;
	}

	@Override
	public List<PaperQuestion> getPaperQuestionList(Integer id) {
		String sql = "SELECT * FROM EXM_PAPER_QUESTION WHERE PAPER_ID = ? ORDER BY NO ASC";
		return getList(sql, new Object[]{id}, PaperQuestion.class);
	}

	@Override
	public List<Question> getQuestionList(Integer id) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_PAPER_QUESTION PAPER_QUESTION "
				+ "INNER JOIN EXM_QUESTION QUESTION ON PAPER_QUESTION.QUESTION_ID = QUESTION.ID "
				+ "WHERE PAPER_QUESTION.PAPER_ID = ? AND PAPER_QUESTION.TYPE = 2";// AND QUESTION.STATE = 1 如果删除试题，试卷关联的试题也能看
		return getList(sql, new Object[]{id}, Question.class);
	}
}