package com.wcpdoc.base.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.core.util.SqlUtil.Order;

/**
 * 用户数据访问层实现
 * 
 * v1.0 zhanghc 2016-6-15下午17:24:19
 */
@Repository
public class UserDaoImpl extends RBaseDaoImpl<User> implements UserDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME, USER.LOGIN_NAME, USER.ORG_ID, ORG.NAME AS ORG_NAME, "
				+ "USER.PHONE AS PHONE, USER.REGIST_TIME, USER.LAST_LOGIN_TIME, USER.ROLES "
				+ "FROM SYS_USER USER " 
				+ "INNER JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID ";
				
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "USER.NAME LIKE ?", String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("orgName")), "ORG.NAME LIKE ?", String.format("%%%s%%", pageIn.get("orgName")))
				.addWhere("USER.STATE != ?", 0)
				.addOrder("USER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "registTime", DateUtil.FORMAT_DATE_TIME, "lastLoginTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public User getUser(String loginName) {
		String sql = "SELECT * FROM SYS_USER WHERE LOGIN_NAME = ? AND STATE = 1";
		return getEntity(sql, new Object[] { loginName });
	}

	@Override
	public boolean existLoginName(String loginName, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM SYS_USER WHERE LOGIN_NAME = ? AND STATE = 1";
			return getCount(sql, new Object[] { loginName}) > 0;
		}
		
		String sql = "SELECT COUNT(*) AS NUM FROM SYS_USER WHERE LOGIN_NAME = ? AND STATE = 1 AND ID != ?";
		return getCount(sql, new Object[] { loginName, excludeId }) > 0;
	}

	@Override
	public List<User> getList(Integer orgId) {
		String sql = "SELECT * FROM SYS_USER WHERE ORG_ID = ? AND STATE = 1";
		return getList(sql, new Object[] { orgId });
	}
}
