package com.wcpdoc.exam.report.dao.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.UserDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.report.dao.GradeDao;

/**
 * 公告数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class GradeDaoImpl extends RBaseDaoImpl<Object> implements GradeDao {
	@Resource
	private UserDao userDao;

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<Map<String, Object>> count(Integer examId) {
		String sql = "SELECT EXM_PAPER.TOTAL_SCORE AS PAPER_TOTAL_SCORE, EXM_PAPER.PASS_SCORE AS PAPER_PASS_SCORE, EXM_EXAM.START_TIME AS EXAM_START_TIME, "
				+ "EXM_EXAM.END_TIME AS EXAM_END_TIME, MAX( MY_EXAM.TOTAL_SCORE ) AS MAX, MIN( MY_EXAM.TOTAL_SCORE ) AS MIN, AVG( MY_EXAM.TOTAL_SCORE ) AS AVG, "
				+ "SUM( MY_EXAM.ANSWER_STATE = 1 ) AS EXAM_USER_ANSWER, COUNT( MY_EXAM.ID	) AS EXAM_USER_SUM, MAX( MY_EXAM.ANSWER_END_TIME ) AS MAX_EXAM, "
				+ "MIN( MY_EXAM.ANSWER_END_TIME ) AS MIN_EXAM FROM EXM_MY_EXAM MY_EXAM "
				+ "INNER JOIN EXM_EXAM ON EXM_EXAM.ID = MY_EXAM.EXAM_ID "
				+ "INNER JOIN EXM_PAPER ON EXM_PAPER.ID = EXM_EXAM.PAPER_ID "
				+ "WHERE MY_EXAM.EXAM_ID = ?";
 		return getMapList(sql, new Object[] { examId });
	}
}