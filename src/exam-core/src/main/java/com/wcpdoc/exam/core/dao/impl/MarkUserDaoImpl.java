package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.MarkUserDao;
import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.MarkUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 判卷用户数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MarkUserDaoImpl extends BaseDaoImpl<MarkUser> implements MarkUserDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public void del(Integer roomId, Integer userId) {
		String sql = "DELETE FROM EXM_MARK_USER WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, new Object[]{roomId, userId});
	}

	@Override
	public MarkUser getEntity(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_MARK_USER WHERE EXAM_ID = ? AND USER_ID = ?";
		return getUnique(sql, new Object[]{examId, userId}, MarkUser.class);
	}
}