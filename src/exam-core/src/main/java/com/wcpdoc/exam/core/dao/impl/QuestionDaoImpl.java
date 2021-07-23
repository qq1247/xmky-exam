package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.core.dao.QuestionDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
		String sql = "SELECT QUESTION.*, QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, UPDATE_USER.NAME AS UPDATE_USER_NAME, CREATE_USER.NAME AS CREATE_USER_NAME "
				+ "FROM EXM_QUESTION QUESTION "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID "
				+ "LEFT JOIN SYS_USER CREATE_USER ON QUESTION.CREATE_USER_ID = CREATE_USER.ID "
				+ "LEFT JOIN SYS_USER UPDATE_USER ON QUESTION.UPDATE_USER_ID = UPDATE_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(pageIn.get("questionTypeId", Integer.class) != null && !"1".equals(pageIn.get("questionTypeId", Integer.class)), "QUESTION.QUESTION_TYPE_ID = ?", pageIn.get("questionTypeId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("id")), "QUESTION.ID = ?", pageIn.get("id", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("title")), "QUESTION.TITLE LIKE ?", "%" + pageIn.get("title") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("state")), "QUESTION.STATE = ?", pageIn.get("state", Integer.class))//0：删除；1：启用；2：禁用
				.addWhere(ValidateUtil.isValid(pageIn.get("type")), "QUESTION.TYPE = ?", pageIn.get("type", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("difficulty")), "QUESTION.DIFFICULTY = ?", pageIn.get("difficulty"))
				.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeName")), "QUESTION_TYPE.NAME LIKE ?", "%" + pageIn.get("questionTypeName") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("score")), "QUESTION.SCORE = ?", pageIn.get("score"))
				.addWhere(ValidateUtil.isValid(pageIn.get("scoreStart")), "QUESTION.SCORE >= ?", pageIn.get("scoreStart"))
				.addWhere(ValidateUtil.isValid(pageIn.get("scoreEnd")), "QUESTION.SCORE <= ?", pageIn.get("scoreEnd"))
				.addWhere(ValidateUtil.isValid(pageIn.get("exPaperId")), "NOT EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = ? AND Z.QUESTION_ID = QUESTION.ID)", pageIn.get("exPaperId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("paperId")), "EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = ? AND Z.QUESTION_ID = QUESTION.ID)", pageIn.get("paperId", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("exAi")) && pageIn.get("exAi").equals("1"), "QUESTION.SCORE_OPTIONS IS NULL")
				.addWhere("QUESTION.STATE != ?", 0)
				.addOrder("QUESTION.ID", Order.ASC);
		
//		if (ValidateUtil.isValid(pageIn.getTen())) {
//			User user = userDao.getEntity(Integer.parseInt(pageIn.getTen()));
//			StringBuilder partSql = new StringBuilder();
//			List<Object> params = new ArrayList<>();
//			partSql.append("(");
//			partSql.append("QUESTION_TYPE.USER_IDS LIKE ? ");
//			params.add("%," + user.getId() + ",%");
//			
//			partSql.append("OR QUESTION_TYPE.ORG_IDS LIKE ? ");
//			params.add("%," + user.getOrgId() + ",%");
//			
//			/*if (ValidateUtil.isValid(user.getPostIds())) {
//				String[] postIds = user.getPostIds().substring(1, user.getPostIds().length() - 1).split(",");
//				for (String postId : postIds) {
//					partSql.append("OR QUESTION_TYPE.POST_IDS LIKE ? ");
//					params.add("%," + postId + ",%");
//				}
//			}*/
//			partSql.append(")");
//			
//			sqlUtil.addWhere(partSql.toString(), params.toArray(new Object[params.size()]));
//		}
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), "QUESTION_TYPE", "TYPE", "QUESTION_DIFFICULTY", "DIFFICULTY", "STATE", "STATE");
//		for(Map<String, Object> map : pageOut.getList()) {
//			String title = map.get("title").toString();
//			Document document = Jsoup.parse(title);
//			Elements embeds = document.getElementsByTag("video");
//			embeds.after("【视频】");
//			embeds.remove();
//			Elements imgs = document.getElementsByTag("img");
//			imgs.after("【图片】");
//			imgs.remove();
//			
//			title = document.body().html();
//			if(title.length() > 500) {
//				title = title.substring(0, 500) + "...";
//			}
//			map.put("title", Jsoup.parse(title).text());
//			
//			String analysis = map.get("analysis").toString();
//			Document documentAnalysis = Jsoup.parse(analysis);
//			Elements embedsAnalysis = documentAnalysis.getElementsByTag("video");
//			embedsAnalysis.after("【视频】");
//			embedsAnalysis.remove();
//			Elements imgsAnalysis = documentAnalysis.getElementsByTag("img");
//			imgsAnalysis.after("【图片】");
//			imgsAnalysis.remove();
//			
//			title = documentAnalysis.body().html();
//			if(analysis.length() > 500) {
//				analysis = analysis.substring(0, 500) + "...";
//			}
//			map.put("analysis", Jsoup.parse(analysis).text());
//		}
		return pageOut;
	}

	@Override
	public QuestionType getQuestionType(Integer id) {
		String sql = "SELECT QUESTION_TYPE.* "
				+ "FROM EXM_QUESTION_TYPE QUESTION_TYPE "
				+ "INNER JOIN EXM_QUESTION QUESTION ON QUESTION_TYPE.ID = QUESTION.QUESTION_TYPE_ID "
				+ "WHERE QUESTION.ID = ?";
		return getEntity(sql, new Object[]{id}, QuestionType.class);
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_QUESTION WHERE STATE !=0 AND QUESTION_TYPE_ID = ?";
		return getList(sql, new Object[] { questionTypeId }, Question.class);
	}

	@Override
	public List<Map<String, Object>> statisticsTypeDifficulty(Integer questionTypeId) {
		String sql = "SELECT COUNT( * ) AS TOTAL, "
				+ "SUM( CASE WHEN TYPE = 1 THEN  TYPE ELSE 0 END) AS TYPE1, "
				+ "SUM( CASE WHEN TYPE = 2 THEN  TYPE ELSE 0 END) AS TYPE2, "
				+ "SUM( CASE WHEN TYPE = 3 THEN  TYPE ELSE 0 END) AS TYPE3, "
				+ "SUM( CASE WHEN TYPE = 4 THEN  TYPE ELSE 0 END) AS TYPE4, "
				+ "SUM( CASE WHEN TYPE = 5 THEN  TYPE ELSE 0 END) AS TYPE5, "
				+ "SUM( CASE WHEN DIFFICULTY = 1 THEN  DIFFICULTY ELSE 0 END) AS DIFFICULTY1, "
				+ "SUM( CASE WHEN DIFFICULTY = 2 THEN  DIFFICULTY ELSE 0 END) AS DIFFICULTY2, "
				+ "SUM( CASE WHEN DIFFICULTY = 3 THEN  DIFFICULTY ELSE 0 END) AS DIFFICULTY3, "
				+ "SUM( CASE WHEN DIFFICULTY = 4 THEN  DIFFICULTY ELSE 0 END) AS DIFFICULTY4, "
				+ "SUM( CASE WHEN DIFFICULTY = 5 THEN  DIFFICULTY ELSE 0 END) AS DIFFICULTY5 "
				+ "FROM EXM_QUESTION  WHERE STATE = 1 "
				+ "AND QUESTION_TYPE_ID = ?";
		return getMapList(sql, new Object[] { questionTypeId });
	}

	@Override
	public List<Map<String, Object>> accuracy(Integer examId) {
		String sql = "SELECT COUNT(ID) AS TOTAL, SUM( SCORE = QUESTION_SCORE ) AS CORRECT, QUESTION_ID FROM EXM_MY_EXAM_DETAIL WHERE EXAM_ID = ? GROUP BY QUESTION_ID ";
		return getMapList(sql, new Object[] { examId });
	}

	@Override
	public PageOut randomListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION.*, QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, UPDATE_USER.NAME AS UPDATE_USER_NAME, CREATE_USER.NAME AS CREATE_USER_NAME "
				+ " from  EXM_QUESTION AS QUESTION "
				+ " LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID "
				+ " LEFT JOIN SYS_USER CREATE_USER ON QUESTION.CREATE_USER_ID = CREATE_USER.ID "
				+ " LEFT JOIN SYS_USER UPDATE_USER ON QUESTION.UPDATE_USER_ID = UPDATE_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("id")), "QUESTION.ID = ?", pageIn.get("id", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("title")), "QUESTION.TITLE LIKE ?", "%" + pageIn.get("title") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("type")), "QUESTION.TYPE = ?", pageIn.get("type", Integer.class))
				.addWhere(ValidateUtil.isValid(pageIn.get("difficulty")), "QUESTION.DIFFICULTY = ?", pageIn.get("difficulty"))
				.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeName")), "QUESTION_TYPE.NAME LIKE ?", "%" + pageIn.get("questionTypeName") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("score")), "QUESTION.SCORE = ?", pageIn.get("score"))
				.addWhere(ValidateUtil.isValid(pageIn.get("exPaperId")), "NOT EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = ? AND Z.QUESTION_ID = QUESTION.ID)", pageIn.get("exPaperId", Integer.class))
				.addWhere("QUESTION.STATE != ?", 0)
				.addOrderRand();
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), "QUESTION_TYPE", "TYPE", "QUESTION_DIFFICULTY", "DIFFICULTY", "STATE", "STATE");
		return pageOut;
}
}