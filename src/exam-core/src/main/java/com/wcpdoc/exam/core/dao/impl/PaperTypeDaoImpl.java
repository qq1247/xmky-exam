package com.wcpdoc.exam.core.dao.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.PaperTypeDao;
import com.wcpdoc.exam.core.entity.PaperType;

/**
 * 试卷分类数据访问层实现
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Repository
public class PaperTypeDaoImpl extends RBaseDaoImpl<PaperType> implements PaperTypeDao {
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT PAPER_TYPE.ID, PAPER_TYPE.NAME, PAPER_TYPE.READ_USER_IDS, "
				+ "PAPER_TYPE.CREATE_USER_ID, USER.NAME AS CREATE_USER_NAME "
				+ "FROM EXM_PAPER_TYPE PAPER_TYPE "
				+ "INNER JOIN SYS_USER USER ON PAPER_TYPE.CREATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "PAPER_TYPE.NAME LIKE :NAME", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("curUserId", Integer.class)), "PAPER_TYPE.READ_USER_IDS LIKE :READ_USER_IDS", String.format("%%,%s,%%", pageIn.get("curUserId", Integer.class)))
				.addWhere("PAPER_TYPE.STATE = 1")
				.addOrder("PAPER_TYPE.UPDATE_TIME", Order.DESC);
		return getListpage(sqlUtil, pageIn);
	}

	@Override
	public List<PaperType> getList(Integer parentId) {
		String sql = "SELECT * FROM EXM_PAPER_TYPE WHERE STATE = 1 AND PARENT_ID = :PARENT_ID";
		return getList(sql, new Object[] { parentId });
	}
}