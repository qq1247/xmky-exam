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
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.exam.dao.PaperDao;
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestion;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.entity.Question;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 试卷数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Repository
public class PaperDaoImpl extends BaseDaoImpl<Paper> implements PaperDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT PAPER.ID, PAPER.NAME, PAPER.TOTLE_SCORE, "
				+ "PAPER_TYPE.NAME AS PAPER_TYPE_NAME, PAPER.STATE "
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
	public PaperType getPaperType(Integer id) {
		String sql = "SELECT PAPER_TYPE.* "
				+ "FROM EXM_PAPER_TYPE PAPER_TYPE "
				+ "INNER JOIN EXM_PAPER PAPER ON PAPER_TYPE.ID = PAPER.PAPER_TYPE_ID "
				+ "WHERE PAPER.ID = ?";
		return getUnique(sql, new Object[]{id}, PaperType.class);
	}

	@Override
	public List<Map<String, Object>> getPaperCfgPaperTreeList(Integer id) {
		String sql = "SELECT PAPER_QUESTION.ID, "
				+ "CASE PAPER_QUESTION.TYPE WHEN '1' THEN PAPER_QUESTION.NAME ELSE QUESTION.TITLE END AS NAME, "
				+ "PAPER_QUESTION.PARENT_ID, PAPER_QUESTION.NO "
				+ "FROM EXM_PAPER_QUESTION PAPER_QUESTION "
				+ "LEFT JOIN EXM_QUESTION QUESTION ON PAPER_QUESTION.QUESTION_ID = QUESTION.ID "
				+ "WHERE PAPER_QUESTION.PAPER_ID = ? "
				+ "ORDER BY PAPER_QUESTION.NO ASC ";
		return getList(sql, new Object[]{id});
	}

	@Override
	public PaperQuestion getRootPaperQuestion(Integer id) {
		String sql = "SELECT * "
				+ "FROM EXM_PAPER_QUESTION PAPER_QUESTION "
				+ "WHERE PAPER_QUESTION.PARENT_ID = 0 AND PAPER_ID = ?";
		return getUnique(sql, new Object[]{id}, PaperQuestion.class);
	}

	@Override
	public PageOut getQuestionListpage(PageIn pageIn) {

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
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "NOT EXISTS (SELECT 1 FROM EXM_PAPER_QUESTION Z WHERE Z.PAPER_ID = ? AND Z.QUESTION_ID = QUESTION.ID)", pageIn.getTen())
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
			/*if(title.length() > 30){
				title = title.substring(0, 30) + "...";
			}*/
			map.put("TITLE", Jsoup.parse(title).text());
		}
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

	@Override
	public List<Paper> getList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER WHERE STATE = 1 AND PAPER_TYPE_ID = ?";
		return getList(sql, new Object[]{paperId}, Paper.class);
	}
}