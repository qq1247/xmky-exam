package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.PaperOptionDao;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PaperOption;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 试卷数据访问层实现
 * 
 * v1.0 chenyun 2021-03-10 13:47:35
 */
@Repository
public class PaperOptionDaoImpl extends RBaseDaoImpl<PaperOption> implements PaperOptionDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT * "
				+ "FROM EXM_PAPER_OPTION PAPER_OPTION ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "PAPER_OPTION.ID = ?", pageIn.getTwo())
				;
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}