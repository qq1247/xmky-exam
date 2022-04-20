package com.wcpdoc.exam.core.dao.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperQuestionRuleDao;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;
import com.wcpdoc.exam.core.entity.Question;

/**
 * 随机章节数据访问层实现
 * 
 * v1.0 chenyun 2022年2月11日 10:51:12
 */
@Repository
public class PaperQuestionRuleDaoImpl extends RBaseDaoImpl<PaperQuestionRule> implements PaperQuestionRuleDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<PaperQuestionRule> getChapterList(Integer paperId, Integer paperQuestionId) {
		String sql = "SELECT *  FROM EXM_PAPER_QUESTION_RULE PAPER_QUESTION_RULE ";
		if (!ValidateUtil.isValid(paperQuestionId)) {
			sql += "WHERE PAPER_QUESTION_RULE.PAPER_ID = :PAPER_ID ORDER BY NO ASC ";
			return getList(sql, new Object[]{ paperId });
		}
		sql += "WHERE PAPER_QUESTION_RULE.PAPER_ID = :PAPER_ID AND PAPER_QUESTION_RULE.PAPER_QUESTION_ID = :PAPER_QUESTION_ID ORDER BY NO ASC ";
		return getList(sql, new Object[]{ paperId, paperQuestionId });
	}
	
	@Override
	public List<Question> getQuestionList(Integer questionTypeId) {
		String sql = "SELECT QUESTION.ID, QUESTION.TYPE, QUESTION.DIFFICULTY, QUESTION.AI "
				+ "FROM EXM_QUESTION QUESTION "
				+ "WHERE QUESTION.QUESTION_TYPE_ID = :QUESTION_TYPE_ID AND QUESTION.STATE = 1 ";
		List<Map<String, Object>> questionMapList = getMapList(sql, new Object[] { questionTypeId });
		List<Question> questionList = new ArrayList<Question>();
		for(Map<String, Object> questionMap : questionMapList){
			Question question = new Question();
			try {
				BeanUtils.populate(question, questionMap);
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new MyException("sql 转换错误");
			}
			questionList.add(question);
		}
		return questionList;
	}
}