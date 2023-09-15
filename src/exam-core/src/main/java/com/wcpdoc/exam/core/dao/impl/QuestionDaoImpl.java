package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
		String sql = "SELECT QUESTION.ID, QUESTION.TYPE, QUESTION.TITLE, "
				+ "QUESTION.MARK_TYPE, QUESTION.STATE, QUESTION.ANALYSIS, QUESTION.QUESTION_TYPE_ID, QUESTION_TYPE.NAME AS QUESTION_TYPE_NAME, "
				+ "QUESTION.SCORE, QUESTION.MARK_OPTIONS, UPDATE_USER.NAME AS UPDATE_USER_NAME "
				+ "FROM EXM_QUESTION QUESTION "
				+ "LEFT JOIN EXM_QUESTION_TYPE QUESTION_TYPE ON QUESTION.QUESTION_TYPE_ID = QUESTION_TYPE.ID "
				+ "LEFT JOIN SYS_USER UPDATE_USER ON QUESTION.UPDATE_USER_ID = UPDATE_USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeId")), "QUESTION.QUESTION_TYPE_ID = :QUESTION_TYPE_ID", pageIn.get("questionTypeId"))
				.addWhere(ValidateUtil.isValid(pageIn.get("questionTypeName")), "QUESTION_TYPE.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("questionTypeName")))
				.addWhere(ValidateUtil.isValid(pageIn.get("id")), "QUESTION.ID = :ID", pageIn.get("id"))
				.addWhere(ValidateUtil.isValid(pageIn.get("title")), "QUESTION.TITLE LIKE :TITLE", String.format("%%%s%%", pageIn.get("title")))
				.addWhere(ValidateUtil.isValid(pageIn.get("type")), "QUESTION.TYPE = :TYPE", pageIn.get("type"))
				.addWhere(ValidateUtil.isValid(pageIn.get("score")), "QUESTION.SCORE = :SCORE", pageIn.get("score"))
				.addWhere(ValidateUtil.isValid(pageIn.get("markType")), "QUESTION.MARK_TYPE = :MARK_TYPE", pageIn.get("markType"))
                .addWhere(ValidateUtil.isValid(pageIn.get("exIds")), "QUESTION.ID NOT IN ("+pageIn.get("exIds")+")")
				.addWhere(!ValidateUtil.isValid(pageIn.get("state")), "QUESTION.STATE = 1")// 默认查询发布和草稿状态
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && "0".equals(pageIn.get("state")), "QUESTION.STATE = 0 AND QUESTION.UPDATE_TIME > :UPDATE_TIME", DateUtil.getNextDay(new Date(), -7))// 查询已删除并且最近7天的试题（回收站使用）
				.addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "QUESTION.STATE = :STATE", pageIn.get("state"))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "QUESTION.CREATE_USER_ID = :CREATE_USER_ID", pageIn.get("curUserId", Integer.class))
				.addOrder(!ValidateUtil.isValid(pageIn.get("rand")), "QUESTION.ID", Order.DESC)// 页面修改的时候，保持原位
				.addOrder(ValidateUtil.isValid(pageIn.get("rand")), "Rand()", Order.NULL);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Integer> getIds(Integer questionTypeId) {
		String sql = "SELECT ID FROM EXM_QUESTION WHERE STATE = 1 AND QUESTION_TYPE_ID = :QUESTION_TYPE_ID";
		List<Map<String, Object>> mapList = getMapList(sql, new Object[] { questionTypeId });
		List<Integer> list = new ArrayList<>(mapList.size());
		for (Map<String, Object> map : mapList) {
			list.add((Integer)map.get("id"));
		}
		return list;
	}

	@Override
	public List<Question> getList(Integer questionTypeId) {
		String sql = "SELECT * FROM EXM_QUESTION WHERE STATE = 1 AND QUESTION_TYPE_ID = :QUESTION_TYPE_ID";
		return getList(sql, new Object[] { questionTypeId });
	}
	
	@Override
	public List<Question> getListByDel() {
		String sql = "SELECT QUESTION.* FROM EXM_QUESTION QUESTION WHERE QUESTION.STATE = 0 AND NOT EXISTS (SELECT 1 FROM EXM_EXAM_QUESTION Z WHERE Z.QUESTION_ID = QUESTION.ID)";
		return getList(sql, new Object[] {});
	}

	@Override
	public int getNum(Integer questionTypeId) {
		String sql = "SELECT COUNT(*) FROM EXM_QUESTION WHERE STATE = 1 AND QUESTION_TYPE_ID = :QUESTION_TYPE_ID";
		return getCount(sql, new Object[] { questionTypeId });
	}

}