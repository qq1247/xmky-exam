package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.dao.ExamRuleDao;
import com.wcpdoc.exam.core.entity.ExamRule;

/**
 * 随机章节数据访问层实现
 * 
 * v1.0 chenyun 2022年2月11日 10:51:12
 */
@Repository
public class ExamRuleDaoImpl extends RBaseDaoImpl<ExamRule> implements ExamRuleDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}
	
	@Override
	public List<ExamRule> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_EXAM_RULE WHERE EXAM_ID = :EXAM_ID ORDER BY NO ASC ";
		return getList(sql, new Object[] { examId });
	}

	@Override
	public void clear(Integer examId) {
		String sql = "DELETE FROM EXM_EXAM_RULE WHERE EXAM_ID = :EXAM_ID";
		update(sql, new Object[] { examId });
	}
}