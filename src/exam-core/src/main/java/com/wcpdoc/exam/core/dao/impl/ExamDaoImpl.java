package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.ExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 考试数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Repository
public class ExamDaoImpl extends RBaseDaoImpl<Exam> implements ExamDao {
	@Resource
	private UserDao userDao;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT EXAM.ID, EXAM.NAME, EXAM.START_TIME, EXAM.END_TIME, EXAM.MARK_START_TIME, EXAM.MARK_END_TIME, "
				+ "EXAM.PASS_SCORE, EXAM.TOTAL_SCORE, EXAM.STATE, EXAM.MARK_STATE, EXAM.SCORE_STATE, EXAM.RANK_STATE, "
				+ "EXAM.GEN_TYPE, EXAM.MARK_TYPE, EXAM.SXES, EXAM.ANON_STATE, "
				+ "(SELECT COUNT(*) FROM EXM_MY_EXAM A WHERE A.EXAM_ID = EXAM.ID) AS USER_NUM "
				+ "FROM EXM_EXAM EXAM ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil
			.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "EXAM.UPDATE_USER_ID = :UPDATE_USER_ID", pageIn.get("curUserId", Integer.class))
			.addWhere(ValidateUtil.isValid(pageIn.get("name")), "EXAM.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("name")))
			.addWhere(!ValidateUtil.isValid(pageIn.get("state")), "EXAM.STATE IN (1,2)")// 默认查询已暂停和已发布
			.addWhere(ValidateUtil.isValid(pageIn.get("state")) && "0".equals(pageIn.get("state")), "EXAM.STATE IN (1,2)")// 如果传入0，会导致查询到已删除的
			.addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "EXAM.STATE = :STATE", pageIn.get("state"))
			.addOrder("EXAM.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(),
				"startTime", DateUtil.FORMAT_DATE_TIME,
				"endTime", DateUtil.FORMAT_DATE_TIME,
				"markStartTime", DateUtil.FORMAT_DATE_TIME,
				"markEndTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}
	
	@Override
	public List<Exam> getList() {
		String sql = "SELECT * FROM EXM_EXAM WHERE STATE != 0";
		return getList(sql, new Object[]{}, Exam.class);
	}
	
	@Override
	public List<Map<String, Object>> getExamUserList(Integer id) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE EXISTS (SELECT 1 "
//				+ "				FROM EXM_MY_EXAM Z "
//				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
//				+ "				AND USER.ID = Z.USER_ID) "
				+ "				FROM EXM_MY_MARK Z "// 不要从我的考试取，如果未发布，里面是空的
				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
				+ "				AND Z.EXAM_USER_IDS LIKE CONCAT('%,', USER.ID, ',%')) "
				+ "ORDER BY USER.UPDATE_TIME DESC ";//USER.STATE = 1 用户被删除也应该显示
		return getMapList(sql, new Object[] { id });
	}

	@Override
	public List<Map<String, Object>> getExamUserList(Integer id, Integer markUserId) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME, ORG.NAME AS ORG_NAME "
				+ "FROM SYS_USER USER "
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_MY_MARK Z "
				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
				+ "					AND Z.MARK_USER_ID = :MARK_USER_ID "
				+ "					AND Z.EXAM_USER_IDS LIKE CONCAT( '%,' , USER.ID , ',%' )) "//回显的情况下，用户状态!=1的也查询
				+ "ORDER BY USER.UPDATE_TIME DESC ";
		return getMapList(sql, new Object[]{id, markUserId});
	}
	
	@Override
	public List<Question> getQuestionList(Integer id) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_EXAM_QUESTION Z "
				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
				+ "					AND Z.QUESTION_ID = QUESTION.ID)";// AND QUESTION.STATE = 1 如果删除试题，试卷关联的试题也能看
		return getList(sql, new Object[] { id }, Question.class);
	}
	
	@Override
	public List<Question> getQuestionList(Integer examId, Integer userId) {
		String sql = "SELECT QUESTION.* "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_EXAM_QUESTION Z "
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
				+ "				FROM EXM_EXAM_QUESTION Z "
				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
				+ "					AND Z.QUESTION_ID = QUESTION_OPTION.QUESTION_ID)"
				+ "ORDER BY QUESTION_OPTION.NO ASC";
		return getList(sql, new Object[] { id }, QuestionOption.class);
	}
	
	@Override
	public List<QuestionOption> getQuestionOptionList(Integer examId, Integer userId) {
		String sql = "SELECT QUESTION_OPTION.* "
				+ "FROM EXM_QUESTION_OPTION QUESTION_OPTION "
				+ "WHERE EXISTS (SELECT 1 "
				+ "				FROM EXM_EXAM_QUESTION Z "
				+ "				WHERE Z.EXAM_ID = :EXAM_ID "
				+ "					AND Z.USER_ID = :USER_ID "
				+ "					AND Z.QUESTION_ID = QUESTION_OPTION.QUESTION_ID)"
				+ "ORDER BY QUESTION_OPTION.NO ASC";
		return getList(sql, new Object[] { examId, userId }, QuestionOption.class);
	}

	@Override
	public List<Map<String, Object>> getExamAnswerList(Integer id) {
		String sql = "SELECT QUESTION_ANSWER.QUESTION_ID, QUESTION_ANSWER.ANSWER, QUESTION_ANSWER.NO, EXAM_QUESTION.SCORES "
				+ "FROM EXM_QUESTION_ANSWER QUESTION_ANSWER "
				+ "LEFT JOIN "
				+ "EXM_EXAM_QUESTION EXAM_QUESTION ON QUESTION_ANSWER.QUESTION_ID = EXAM_QUESTION.QUESTION_ID "
				+ "WHERE EXISTS ( SELECT 1 FROM EXM_EXAM_QUESTION Z WHERE Z.EXAM_ID = :EXAM_ID "
				+ "AND Z.QUESTION_ID = QUESTION_ANSWER.QUESTION_ID ) "
				+ "ORDER BY QUESTION_ANSWER.NO ASC ";
		return getMapList(sql, new Object[] { id });
	}

	@Override
	public List<Map<String, Object>> getExamAnswerList(Integer examId, Integer userId) {
		String sql = "SELECT QUESTION_ANSWER.QUESTION_ID, QUESTION_ANSWER.ANSWER, QUESTION_ANSWER.NO, EXAM_QUESTION.SCORES "
				+ "FROM EXM_QUESTION_ANSWER QUESTION_ANSWER "
				+ "LEFT JOIN "
				+ "EXM_EXAM_QUESTION EXAM_QUESTION ON QUESTION_ANSWER.QUESTION_ID = EXAM_QUESTION.QUESTION_ID "
				+ "WHERE EXISTS ( SELECT 1 FROM EXM_EXAM_QUESTION Z WHERE Z.EXAM_ID = :EXAM_ID AND Z.USER_ID = :USER_ID "
				+ "AND Z.QUESTION_ID = QUESTION_ANSWER.QUESTION_ID ) "
				+ "ORDER BY QUESTION_ANSWER.NO ASC ";
		return getMapList(sql, new Object[] { examId, userId });
	}
}