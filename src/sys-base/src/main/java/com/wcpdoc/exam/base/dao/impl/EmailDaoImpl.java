package com.wcpdoc.exam.base.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.dao.EmailDao;
import com.wcpdoc.exam.base.entity.Email;
import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 邮箱数据访问层实现
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Repository
public class EmailDaoImpl extends RBaseDaoImpl<Email> implements EmailDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT * "
				+ "FROM SYS_EMAIL EMAIL ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "EMAIL.ID = ?", pageIn.getTwo())
				;
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public Email getEmail(Integer userId) {
		String sql = "SELECT * FROM SYS_EMAIL WHERE UPDATE_USER_ID = ?";
		return getEntity(sql, new Object[] { userId });
	}
}