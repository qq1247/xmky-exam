package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

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
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;

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
		String sql = "SELECT * "
				+ "FROM (SELECT PAPER_TYPE.ID, PAPER_TYPE.NAME, PAPER_TYPE.STATE, NULL AS PASS_SCORE, NULL AS TOTAL_SCORE, NULL AS PAPER_TYPE_ID, "// PAPER_TYPE_ID==null，有分类ID查询时只显示试卷
				+ "		NULL AS MARK_TYPE, NULL AS SHOW_TYPE, NULL AS GEN_TYPE, PAPER_TYPE.READ_USER_IDS, PAPER_TYPE.UPDATE_TIME, 1 AS TYPE "
				+ "		FROM EXM_PAPER_TYPE PAPER_TYPE "
				+ "		WHERE PAPER_TYPE.READ_USER_IDS LIKE :READ_USER_IDS "// 查找当前用户有权限的分类
				+ "		UNION ALL "// 试卷分类和无分类的试卷合并显示默认展示在第一层，用于增加用户体验
				+ "		SELECT PAPER.ID, PAPER.NAME, PAPER.STATE, PAPER.PASS_SCORE, PAPER.TOTAL_SCORE, PAPER.PAPER_TYPE_ID, "
				+ "		PAPER.MARK_TYPE, PAPER.SHOW_TYPE, PAPER.GEN_TYPE, PAPER.READ_USER_IDS, PAPER.UPDATE_TIME, 2 AS TYPE "
				+ "		FROM EXM_PAPER PAPER "
				+ "		WHERE PAPER.READ_USER_IDS LIKE :READ_USER_IDS2 AND PAPER.PAPER_TYPE_ID IS NULL"// 查找当前用户有权限并且无分类的试卷（READ_USER_IDS2 参数不能重复）
				+ ") A";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addFromParm(String.format("%%,%s,%%", pageIn.get("curUserId", Integer.class)))
				.addFromParm(String.format("%%,%s,%%", pageIn.get("curUserId", Integer.class)))
				.addWhere(ValidateUtil.isValid(pageIn.get("paperTypeId")), "A.PAPER_TYPE_ID = :PAPER_TYPE_ID", pageIn.get("paperTypeId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("name")), "A.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(!ValidateUtil.isValid(pageIn.get("state")), "A.STATE IN (1,2)")// 默认查询草稿和已发布
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && "0".equals(pageIn.get("state")), "A.STATE IN (1,2)")// 如果传入0，会导致查询到已删除的
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "A.STATE = :STATE", pageIn.get("state"))
				.addOrder("A.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		for (Map<String, Object> result : pageOut.getList()) {
			result.remove("readUserIds");// 剔除非接口字段
			result.remove("updateTime");
		}
		return pageOut;
	}

	public static void main(String[] args) {
		String format = String.format("%%,%s,%%", new Integer(1));
		System.err.println(format);
	}
	
	@Override
	public List<Paper> getList(Integer paperTypeId) {
		String sql = "SELECT * FROM EXM_PAPER WHERE STATE != 0 AND PAPER_TYPE_ID = :PAPER_TYPE_ID";
		return getList(sql, new Object[] { paperTypeId }, Paper.class);
	}

	@Override
	public List<Question> getQuestionList(Integer id) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_PAPER_QUESTION Z "
				+ "				WHERE Z.PAPER_ID = :PAPER_ID "
				+ "					AND Z.QUESTION_ID = QUESTION.ID)";// AND QUESTION.STATE = 1 如果删除试题，试卷关联的试题也能看
		return getList(sql, new Object[] { id }, Question.class);
	}
	
	@Override
	public List<Question> getQuestionList(Integer examId, Integer userId) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_PAPER_QUESTION Z "
				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
				+ "					AND Z.USER_ID = :USER_ID "
				+ "					AND Z.QUESTION_ID = QUESTION.ID)";// AND QUESTION.STATE = 1 如果删除试题，试卷关联的试题也能看
		return getList(sql, new Object[] { examId, userId }, Question.class);
	}

	@Override
	public List<QuestionOption> getQuestionOptionList(Integer id) {
		String sql = "SELECT QUESTION_OPTION.* "
				+ "FROM EXM_QUESTION_OPTION QUESTION_OPTION "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_PAPER_QUESTION Z "
				+ "				WHERE Z.PAPER_ID = :PAPER_ID "
				+ "					AND Z.QUESTION_ID = QUESTION_OPTION.QUESTION_ID)"
				+ "ORDER BY QUESTION_OPTION.NO ASC";
		return getList(sql, new Object[] { id }, QuestionOption.class);
	}

	@Override
	public List<QuestionOption> getQuestionOptionList(Integer examId, Integer userId) {
		String sql = "SELECT QUESTION_OPTION.* "
				+ "FROM EXM_QUESTION_OPTION QUESTION_OPTION "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_PAPER_QUESTION Z "
				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
				+ "					AND Z.USER_ID = :USER_ID "
				+ "					AND Z.QUESTION_ID = QUESTION_OPTION.QUESTION_ID)"
				+ "ORDER BY QUESTION_OPTION.NO ASC";
		return getList(sql, new Object[] { examId, userId }, QuestionOption.class);
	}

	@Override
	public List<Paper> getList() {
		String sql = "SELECT * FROM EXM_PAPER WHERE STATE != 0";
		return getList(sql, Paper.class);
	}
}