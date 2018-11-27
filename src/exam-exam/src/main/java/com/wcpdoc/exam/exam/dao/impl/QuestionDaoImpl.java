package com.wcpdoc.exam.exam.dao.impl;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.exam.dao.QuestionDao;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.exam.entity.QuestionType;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 试题数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Repository
public class QuestionDaoImpl extends BaseDaoImpl<Question> implements QuestionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION.ID, QUESTION.ID AS CODE, QUESTION.TITLE, QUESTION.TYPE, QUESTION.DIFFICULTY, "
				+ "QUESTION.STATE, QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME "
				+ "FROM EXM_QUESTION QUESTION "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID ";
		
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getOne()) && !"1".equals(pageIn.getOne()), "QUESTION.QUESTION_TYPE_ID = ?", pageIn.getOne())
				.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "QUESTION.ID = ?", pageIn.getTwo())
				.addWhere(ValidateUtil.isValid(pageIn.getThree()), "QUESTION.TITLE LIKE ?", "%" + pageIn.getThree() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getFour()), "QUESTION.STATE = ?", pageIn.getFour())//0：删除；1：启用；2：禁用
				.addWhere(ValidateUtil.isValid(pageIn.getFive()), "QUESTION.TYPE = ?", pageIn.getFive())
				.addWhere(ValidateUtil.isValid(pageIn.getSix()), "QUESTION.DIFFICULTY = ?", pageIn.getSix())
				.addWhere(ValidateUtil.isValid(pageIn.getEight()), 
						"(QUESTION_TYPE.USER_IDS LIKE ? "
								+ "OR EXISTS (SELECT 1 FROM SYS_USER Z WHERE Z.ID = ? AND QUESTION_TYPE.ORG_IDS LIKE CONCAT('%,', Z.ORG_ID, ',%')) "
								+ "OR EXISTS (SELECT 1 FROM SYS_POST_USER Z WHERE Z.USER_ID = ? AND QUESTION_TYPE.POST_IDS LIKE CONCAT('%,', Z.POST_ID, ',%')))", 
						"%," + pageIn.getEight() + ",%", pageIn.getEight(), pageIn.getEight())
				.addWhere("QUESTION.STATE != ?", 0)
				.addOrder("QUESTION.UPDATE_TIME", Order.DESC);
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDict(pageOut.getRows(), DictCache.getIndexkeyValueMap(), "QUESTION_TYPE", "TYPE", "QUESTION_DIFFICULTY", "DIFFICULTY", "STATE", "STATE");
		for(Map<String, Object> map : pageOut.getRows()){
			String title = map.get("TITLE").toString();
			Document document = Jsoup.parse(title);
			Elements embeds = document.getElementsByTag("embed");
			embeds.after("【视频】");
			embeds.remove();
			Elements imgs = document.getElementsByTag("img");
			imgs.after("【图片】");
			imgs.remove();
			
			title = document.body().html();
			if(title.length() > 500){
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
		return getUnique(sql, new Object[]{id}, QuestionType.class);
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_QUESTION WHERE STATE = 1 AND QUESTION_TYPE_ID = ?";
		return getList(sql, new Object[]{questionTypeId}, Question.class);
	}
}