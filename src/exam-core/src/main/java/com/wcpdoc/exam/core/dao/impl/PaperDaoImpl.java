package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.cache.DictCache;
import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperDao;
import com.wcpdoc.exam.core.entity.Paper;
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
		String sql = "SELECT PAPER.ID, PAPER.NAME, PAPER.STATE, PAPER.UPDATE_USER_ID, "
				+ "PAPER.PASS_SCORE, PAPER.TOTAL_SCORE, PAPER.PAPER_TYPE_ID, PAPER.SHOW_TYPE,"
				+ "PAPER.GEN_TYPE, PAPER_TYPE.NAME AS PAPER_TYPE_NAME, "
				+ "UPDATE_USER.NAME AS UPDATE_USER_NAME "
				+ "FROM EXM_PAPER PAPER "
				+ "LEFT JOIN EXM_PAPER_TYPE PAPER_TYPE ON PAPER.PAPER_TYPE_ID = PAPER_TYPE.ID "
				+ "LEFT JOIN SYS_USER UPDATE_USER ON PAPER.UPDATE_USER_ID = UPDATE_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("paperTypeId")), "PAPER.PAPER_TYPE_ID = ?", pageIn.get("paperTypeId"))
		.addWhere(ValidateUtil.isValid(pageIn.get("name")), "PAPER.NAME LIKE ?", "%" + pageIn.get("name") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "PAPER_TYPE.READ_USER_IDS LIKE ?", String.format("%%%s%%", pageIn.get("curUserId", Integer.class)))// 只看自己的
				.addWhere("PAPER.STATE IN (1,2)")
				.addOrder("PAPER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), "STATE", "STATE");
		return pageOut;
	}

	@Override
	public List<Question> getQuestionList(Integer id) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_PAPER_QUESTION PAPER_QUESTION "
				+ "INNER JOIN EXM_QUESTION QUESTION ON PAPER_QUESTION.QUESTION_ID = QUESTION.ID "
				+ "WHERE PAPER_QUESTION.PAPER_ID = ? AND PAPER_QUESTION.TYPE = 2";// AND QUESTION.STATE = 1 如果删除试题，试卷关联的试题也能看
		return getList(sql, new Object[]{id}, Question.class);
	}

	@Override
	public List<Paper> getList(Integer paperTypeId) {
		String sql = "SELECT * FROM EXM_PAPER WHERE STATE != 0 AND PAPER_TYPE_ID = ?";
		return getList(sql, new Object[] { paperTypeId }, Paper.class);
	}
}