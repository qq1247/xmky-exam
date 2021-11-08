package com.wcpdoc.exam.core.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.cache.DictCache;
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
import com.wcpdoc.exam.core.dao.BulletinDao;
import com.wcpdoc.exam.core.entity.Bulletin;

/**
 * 公告数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class BulletinDaoImpl extends RBaseDaoImpl<Bulletin> implements BulletinDao {
	@Resource
	private UserDao userDao;
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT BULLETIN.ID, BULLETIN.TITLE, BULLETIN.SHOW_TYPE, BULLETIN.UPDATE_TIME, "
				+ "BULLETIN.IMG_FILE_ID, BULLETIN.CONTENT, BULLETIN.STATE, USER.NAME AS UPDATE_USER_NAME, "
				+ "( SELECT GROUP_CONCAT( Z.NAME ) FROM SYS_USER Z WHERE BULLETIN.READ_USER_IDS LIKE CONCAT( '%,', Z.ID, ',%' ) ) AS READ_USER_NAMES "
				+ "FROM EXM_BULLETIN BULLETIN "
				+ "LEFT JOIN SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("id")), "BULLETIN.ID = ?", pageIn.get("id"))
			   .addWhere(ValidateUtil.isValid(pageIn.get("title")), "BULLETIN.TITLE LIKE ?", "%" + pageIn.get("title") + "%")
			   .addWhere(ValidateUtil.isValid(pageIn.get("showType")), "BULLETIN.SHOW_TYPE = ?", pageIn.get("showType", Integer.class))
			   .addWhere(pageIn.get("curUserId", Integer.class)!= null, "BULLETIN.UPDATE_USER_ID = ?", pageIn.get("curUserId", Integer.class))
			   .addWhere(!ValidateUtil.isValid(pageIn.get("state")), "BULLETIN.STATE IN (1, 2)", pageIn.get("state"))
			   .addWhere(ValidateUtil.isValid(pageIn.get("state")), "BULLETIN.STATE = ?", pageIn.get("state"))
			   .addOrder("BULLETIN.UPDATE_TIME", Order.DESC);
		  if (pageIn.get("readUserIds", Integer.class) != null) {
			   User user = userDao.getEntity(pageIn.get("readUserIds", Integer.class));
			   StringBuilder partSql = new StringBuilder();
			   List<Object> params = new ArrayList<>();
			   partSql.append("(");
			   partSql.append("BULLETIN.READ_USER_IDS IS NULL OR BULLETIN.READ_USER_IDS LIKE ? ");
			   params.add("%," + user.getId() + ",%");
			   partSql.append(")");
			   sqlUtil.addWhere(partSql.toString(), params.toArray(new Object[params.size()]));
			  }
		
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "updateTime", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), 
				"BULLETIN_SHOW_TYPE", "showType");
		return pageOut;
	}

	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME "
				+ "FROM SYS_USER USER ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "(USER.NAME LIKE ? )", "%" + pageIn.get("name") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("id")), "EXISTS (SELECT 1 FROM EXM_BULLETIN Z WHERE Z.ID = ? AND Z.READ_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.get("id"))
				.addWhere("USER.STATE = 1")
				.addOrder("USER.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public PageOut getOrgListpage(PageIn pageIn) {
		String sql = "SELECT ORG.ID, ORG.NAME AS NAME "
				+ "FROM SYS_ORG ORG ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "(ORG.NAME LIKE ? )", "%" + pageIn.get("name") + "%")
				.addWhere(ValidateUtil.isValid(pageIn.get("id")), "EXISTS (SELECT 1 FROM EXM_BULLETIN Z WHERE Z.ID = ? AND Z.READ_ORG_IDS LIKE CONCAT('%,', ORG.ID, ',%'))", pageIn.get("id"))
				.addWhere("ORG.STATE = 1")
				.addOrder("ORG.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}

	@Override
	public List<Map<String, Object>> get(Integer id) {
		String sql = "SELECT BULLETIN.ID, BULLETIN.TITLE, BULLETIN.TOP_STATE, BULLETIN.UPDATE_TIME, BULLETIN.IMG_FILE_ID, BULLETIN.CONTENT, BULLETIN.STATE, "
				+ "USER.NAME AS UPDATE_USER_NAME, BULLETIN.READ_USER_IDS, BULLETIN.READ_ORG_IDS, "
				+ "IFNULL((SELECT GROUP_CONCAT( USER.NAME SEPARATOR ',' ) FROM sys_user USER WHERE USER.state != 0 AND EXISTS ( SELECT 1 FROM EXM_BULLETIN et WHERE BULLETIN.ID = et.ID AND et.READ_USER_IDS LIKE CONCAT( '%,' , USER.ID, ',%' ))),'') AS 'READ_USER_NAMES', "
				+ "IFNULL((SELECT GROUP_CONCAT( ORG.NAME SEPARATOR ',' ) FROM sys_org ORG WHERE ORG.state != 0 AND EXISTS ( SELECT 1 FROM EXM_BULLETIN et WHERE BULLETIN.ID = et.ID AND et.READ_ORG_IDS LIKE CONCAT( '%,' , USER.ID, ',%' ))),'') AS 'READ_ORG_NAMES' "
				+ "FROM EXM_BULLETIN BULLETIN LEFT JOIN SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID WHERE BULLETIN.ID = ? ";
		return getMapList(sql, new Object[]{id});
	}
}