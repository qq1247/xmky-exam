package com.wcpdoc.exam.exam.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.BaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.dao.ExamTypeAuthDao;
import com.wcpdoc.exam.exam.entity.ExamTypeAuth;

/**
 * 考试分类权限数据访问层实现
 * 
 * v1.0 zhanghc 2018年5月29日下午3:20:16
 */
@Repository
public class ExamTypeAuthDaoImpl extends BaseDaoImpl<ExamTypeAuth> implements ExamTypeAuthDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		return null;
	}

	@Override
	public List<ExamTypeAuth> getList() {
		String sql = "SELECT * FROM EXM_EXAM_TYPE_AUTH";
		return getList(sql, ExamTypeAuth.class);
	}
}