package com.wcpdoc.exam.exam.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.dao.ExamUserDao;
import com.wcpdoc.exam.exam.entity.ExamUser;

/**
 * 考试用户数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class ExamUserDaoImpl extends BaseDaoImpl<ExamUser> implements ExamUserDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public void delete(Integer roomId, Integer userId) {
		String sql = "DELETE FROM EX_EXAM_USER WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, new Object[]{roomId, userId});
	}

	@Override
	public ExamUser getEntity(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EX_EXAM_USER WHERE EXAM_ID = ? AND USER_ID = ?";
		return getUnique(sql, new Object[]{examId, userId}, ExamUser.class);
	}
}