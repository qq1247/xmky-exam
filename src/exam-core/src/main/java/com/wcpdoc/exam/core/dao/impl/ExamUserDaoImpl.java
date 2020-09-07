package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.ExamUserDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.ExamUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 考试用户数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class ExamUserDaoImpl extends RBaseDaoImpl<ExamUser> implements ExamUserDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public void del(Integer roomId, Integer userId) {
		String sql = "DELETE FROM EXM_EXAM_USER WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, new Object[]{roomId, userId});
	}

	@Override
	public ExamUser getEntity(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_EXAM_USER WHERE EXAM_ID = ? AND USER_ID = ?";
		return getEntity(sql, new Object[]{examId, userId}, ExamUser.class);
	}
}