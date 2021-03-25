package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.base.entity.User;
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
		String sql = "SELECT QUESTION.*, QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, USER.NAME AS QUESTION_USER_NAME "
				+ "FROM EXM_QUESTION QUESTION "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID "
				+ "LEFT JOIN SYS_USER USER ON QUESTION.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "QUESTION.QUESTION_TYPE_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "QUESTION.ID = ?", pageIn.getTwo())
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "QUESTION.TITLE LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getFour()), "QUESTION.STATE = ?", pageIn.getFour())//0：删除；1：启用；2：禁用
				.addWhere(ValidateUtil.isValid(pageIn.getFive()), "QUESTION.TYPE = ?", pageIn.getFive())
				.addWhere(ValidateUtil.isValid(pageIn.getSix()), "QUESTION.DIFFICULTY = ?", pageIn.getSix())
				.addWhere(ValidateUtil.isValid(pageIn.getSeven()), "QUESTION_TYPE.NAME LIKE ?", "%" + pageIn.getSeven() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getEight()), "QUESTION.SCORE = ?", pageIn.getEight())
				.addWhere(ValidateUtil.isValid(pageIn.getSortOne()), "QUESTION.SCORE >= ?", pageIn.getSortOne())
				.addWhere(ValidateUtil.isValid(pageIn.getSortTwo()), "QUESTION.SCORE <= ?", pageIn.getSortTwo())
				.addWhere(ValidateUtil.isValid(pageIn.getNine()), "NOT EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = ? AND Z.QUESTION_ID = QUESTION.ID)", pageIn.getNine())
				.addWhere("QUESTION.STATE != ?", 0)
				.addOrder("QUESTION.NO", Order.DESC);
		
		if (ValidateUtil.isValid(pageIn.getTen())) {
			User user = userDao.getEntity(Integer.parseInt(pageIn.getTen()));
			StringBuilder partSql = new StringBuilder();
			List<Object> params = new ArrayList<>();
			partSql.append("(");
			partSql.append("QUESTION_TYPE.USER_IDS LIKE ? ");
			params.add("%" + user.getId() + "%");
			
			partSql.append("OR QUESTION_TYPE.ORG_IDS LIKE ? ");
			params.add("%" + user.getOrgId() + "%");
			
			if (ValidateUtil.isValid(user.getPostIds())) {
				String[] postIds = user.getPostIds().substring(1, user.getPostIds().length() - 1).split(",");
				for (String postId : postIds) {
					partSql.append("OR QUESTION_TYPE.POST_IDS LIKE ? ");
					params.add("%" + postId + "%");
				}
			}
			partSql.append(")");
			
			sqlUtil.addWhere(partSql.toString(), params.toArray(new Object[params.size()]));
		}
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "QUESTION_TYPE", "TYPE", "QUESTION_DIFFICULTY", "DIFFICULTY", "STATE", "STATE");
		for(Map<String, Object> map : pageOut.getRows()) {
			String title = map.get("TITLE").toString();
			Document document = Jsoup.parse(title);
			Elements embeds = document.getElementsByTag("video");
			embeds.after("【视频】");
			embeds.remove();
			Elements imgs = document.getElementsByTag("img");
			imgs.after("【图片】");
			imgs.remove();
			
			title = document.body().html();
			if(title.length() > 500) {
				title = title.substring(0, 500) + "...";
			}
			map.put("TITLE", Jsoup.parse(title).text());
		}
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
		String sql = "SELECT * FROM EXM_QUESTION WHERE STATE = 1 AND QUESTION_TYPE_ID = ?";
		return getList(sql, new Object[] { questionTypeId }, Question.class);
	}

	@Override
	public List<Map<String, Object>> count(Integer questionTypeId) {
		String sql = "SELECT COUNT(*) AS TOTAL, SUM(TYPE = 1) AS T1, SUM(TYPE = 2) AS T2, SUM(TYPE = 3) AS T3, SUM(TYPE = 4) AS T4, SUM(TYPE = 5) AS T5, "
				+ "SUM(DIFFICULTY = 1) AS D1, SUM(DIFFICULTY = 2) AS D2, SUM(DIFFICULTY = 3) AS D3, SUM(DIFFICULTY = 4) AS D4, SUM(DIFFICULTY = 5) AS D5 "
				+ "FROM EXM_QUESTION WHERE STATE = 1 AND QUESTION_TYPE_ID = ?";
		return getMapList(sql, new Object[] { questionTypeId });
	}

	@Override
	public List<Map<String, Object>> accuracy(Integer examId) {
		String sql = "SELECT COUNT(ID) AS TOTAL, SUM( SCORE = QUESTION_SCORE ) AS CORRECT, QUESTION_ID FROM EXM_MY_EXAM_DETAIL WHERE EXAM_ID = ? GROUP BY QUESTION_ID ";
		return getMapList(sql, new Object[] { examId });
	}
}