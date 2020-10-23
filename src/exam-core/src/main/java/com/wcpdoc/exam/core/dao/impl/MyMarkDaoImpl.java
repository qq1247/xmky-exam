package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 我的阅卷数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyMarkDaoImpl extends RBaseDaoImpl<MyMark> implements MyMarkDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public void del(Integer roomId, Integer userId) {
		String sql = "DELETE FROM EXM_MY_MARK WHERE EXAM_ID = ? AND USER_ID = ?";
		update(sql, new Object[]{roomId, userId});
	}

	@Override
	public MyMark getEntity(Integer examId, Integer userId) {
		String sql = "SELECT * FROM EXM_MY_MARK WHERE EXAM_ID = ? AND USER_ID = ?";
		return getEntity(sql, new Object[]{examId, userId}, MyMark.class);
	}

	@Override
	public List<MyMark> getList(Integer examId) {
		String sql = "SELECT * FROM EXM_MY_MARK WHERE EXAM_ID = ?";
		return getList(sql, new Object[] { examId }, MyMark.class);
	}
}