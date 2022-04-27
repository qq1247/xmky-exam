package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperRemarkDao;
import com.wcpdoc.exam.core.entity.PaperRemark;


/**
 * 试卷评语数据访问层实现
 * 
 * v1.0 chenyun 2021-03-10 13:48:34
 */
@Repository
public class PaperRemarkDaoImpl extends RBaseDaoImpl<PaperRemark> implements PaperRemarkDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT * FROM EXM_PAPER_REMARK PAPER_REMARK ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("id").toString()), "PAPER_REMARK.ID = :ID", pageIn.get("id").toString());
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<PaperRemark> getList(Integer paperId) {
		String sql = "SELECT * FROM EXM_PAPER_REMARK WHERE PAPER_ID = :PAPER_ID";
		return getList(sql, new Object[]{ paperId });
	}
}