package com.wcpdoc.exam.core.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.dao.QuestionCommentDao;
import com.wcpdoc.exam.core.entity.QuestionComment;

/**
 * 试题评论数据访问层实现
 * 
 * v1.0 chenyun 2021年8月31日上午10:01:50
 */
@Repository
public class QuestionCommentDaoImpl extends RBaseDaoImpl<QuestionComment> implements QuestionCommentDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT QUESTION_COMMENT.ID, QUESTION_COMMENT.CONTENT, QUESTION_COMMENT.CREATE_USER_ID, "
				+ "(SELECT USER.NAME FROM SYS_USER USER WHERE USER.ID = QUESTION_COMMENT.CREATE_USER_ID) as CREATE_USER_NAME, "
				+ "QUESTION_COMMENT.CREATE_TIME FROM EXM_QUESTION_COMMENT QUESTION_COMMENT";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("content")), "QUESTION_COMMENT.CONTENT LIKE ?", "%" + pageIn.get("content") + "%")
				.addWhere(pageIn.get("questionId", Integer.class) != null, "QUESTION_COMMENT.QUESTION_ID = ?", pageIn.get("questionId", Integer.class))
				.addWhere(pageIn.get("parentId", Integer.class) != null, "QUESTION_COMMENT.PARENT_ID = ?", pageIn.get("parentId", Integer.class))
				.addWhere(pageIn.get("parentId", Integer.class) == null, "QUESTION_COMMENT.PARENT_ID = 0" )
				.addWhere("QUESTION_COMMENT.STATE = 1")
				.addOrder("QUESTION_COMMENT.CREATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "createTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> getList(Integer parentId) {
		String sql = "SELECT PARENT_QUESTION_COMMENT.ID, PARENT_QUESTION_COMMENT.CONTENT, PARENT_QUESTION_COMMENT.CREATE_USER_ID,"
				   + "(SELECT USER.NAME FROM SYS_USER USER WHERE USER.ID = PARENT_QUESTION_COMMENT.CREATE_USER_ID) AS CREATE_USER_NAME, "
				   + "PARENT_QUESTION_COMMENT.CREATE_TIME AS CREATE_TIME "
				   + "FROM EXM_QUESTION_COMMENT PARENT_QUESTION_COMMENT WHERE "
				   + "PARENT_QUESTION_COMMENT.PARENT_ID = ? AND PARENT_QUESTION_COMMENT.STATE = 1 ";
		return getMapList(sql, new Object[] { parentId });
	}	
}