package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.MyExamDetailDao;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;

/**
 * 我的考试详细数据访问层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Repository
public class MyExamDetailDaoImpl extends RBaseDaoImpl<MyExamDetail> implements MyExamDetailDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<MyExamDetail> getList(Integer myExamId) {
		String sql = "SELECT * FROM EXM_MY_EXAM_DETAIL WHERE MY_EXAM_ID = ?";
		return getList(sql, new Object[] { myExamId }, MyExamDetail.class);
	}

	@Override
	public void delByMyExamId(Integer myExamId) {
		String sql = "DELETE FROM EXM_MY_EXAM_DETAIL WHERE MY_EXAM_ID = ?";
		update(sql, new Object[] { myExamId });
	}
}