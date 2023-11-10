package com.wcpdoc.base.dao.impl;

import java.util.List;

import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.wcpdoc.base.dao.UserDao;
import com.wcpdoc.base.entity.User;
import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;

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
				+ "USER.STATE "
				+ "FROM SYS_USER USER " 
				+ "LEFT JOIN SYS_ORG ORG ON USER.ORG_ID = ORG.ID ";
				
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "(USER.NAME LIKE :NAME OR ORG.NAME LIKE :ORG_NAME)", String.format("%%%s%%", pageIn.get("name")), String.format("%%%s%%", pageIn.get("name")))
				.addWhere(ValidateUtil.isValid(pageIn.get("state")), "USER.STATE = :USER_STATE", pageIn.get("state"))
				.addWhere(ValidateUtil.isValid(pageIn.get("type")), "USER.TYPE = :USER_TYPE", pageIn.get("type"))
				.addWhere(ValidateUtil.isValid(pageIn.get("ids")), "USER.ID IN ("+pageIn.get("ids")+")")
				.addWhere(ValidateUtil.isValid(pageIn.get("parentId", Integer.class)), "USER.PARENT_ID = :PARENT_ID", pageIn.get("parentId", Integer.class))
				.addWhere("USER.STATE != 0")
				.addOrder("USER.UPDATE_TIME", Order.DESC)// bug：导入用户时间一致，分页错误
				.addOrder("USER.ID", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "registTime", DateUtil.FORMAT_DATE_TIME, "lastLoginTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public User getUser(String loginName) {
		String sql = "SELECT * FROM SYS_USER WHERE LOGIN_NAME = :LOGIN_NAME AND STATE != 0";
		return getEntity(sql, new Object[] { loginName });
	}

	@Override
	public boolean existLoginName(String loginName, Integer excludeId) {
		if (excludeId == null) {
			String sql = "SELECT COUNT(*) AS NUM FROM SYS_USER WHERE LOGIN_NAME = :LOGIN_NAME AND STATE != 0";
			return getCount(sql, new Object[] { loginName}) > 0;
		}
		
		String sql = "SELECT COUNT(*) AS NUM FROM SYS_USER WHERE LOGIN_NAME = :LOGIN_NAME AND STATE != 0 AND ID != :ID";
		return getCount(sql, new Object[] { loginName, excludeId }) > 0;
	}

	@Override
	public List<User> getList(Integer orgId) {
		String sql = "SELECT * FROM SYS_USER WHERE ORG_ID = :ORG_ID AND STATE != 0";
		return getList(sql, new Object[] { orgId });
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> getList(Integer[] ids) {
		String sql = "SELECT * FROM SYS_USER WHERE ID IN (:IDS) AND STATE != 0";
		Query<User> query = getCurSession().createSQLQuery(sql);
		query.setParameterList("IDS", ids);
		query.unwrap(NativeQuery.class).addEntity(clazz);
		return query.list();
	}

	@Override
	public List<User> getList() {
		String sql = "SELECT * FROM SYS_USER WHERE STATE != 0";
		return getList(sql);
	}

	@Override
	public List<User> getMarkUserlist(Integer parentId) {
		String sql = "SELECT * FROM SYS_USER WHERE TYPE = 3 AND STATE != 0 AND PARENT_ID = :PARENT_ID";
		return getList(sql, new Object[] { parentId });
	}
}
