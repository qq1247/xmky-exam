package com.wcpdoc.exam.core.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.BulletinDao;
import com.wcpdoc.exam.core.entity.Bulletin;

/**
 * 公告数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class BulletinDaoImpl extends RBaseDaoImpl<Bulletin> implements BulletinDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT BULLETIN.ID, BULLETIN.TITLE, BULLETIN.SHOW_TYPE, BULLETIN.UPDATE_TIME, BULLETIN.START_TIME, BULLETIN.END_TIME, "
				+ "BULLETIN.IMG_FILE_ID, BULLETIN.CONTENT, BULLETIN.STATE, USER.NAME AS UPDATE_USER_NAME, "
				+ "( SELECT GROUP_CONCAT( Z.NAME ) FROM SYS_USER Z WHERE BULLETIN.READ_USER_IDS LIKE CONCAT( '%,', Z.ID, ',%' ) ) AS READ_USER_NAMES "
				+ "FROM EXM_BULLETIN BULLETIN "
				+ "LEFT JOIN SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("id")), "BULLETIN.ID = ?", pageIn.get("id"))
			   .addWhere(ValidateUtil.isValid(pageIn.get("title")), "BULLETIN.TITLE LIKE ?", "%" + pageIn.get("title") + "%")
			   .addWhere(pageIn.get("curUserId", Integer.class) == 1, "BULLETIN.UPDATE_USER_ID = ?", pageIn.get("curUserId", Integer.class))
			   .addWhere(pageIn.get("curUserId", Integer.class) != 1, "(BULLETIN.READ_USER_IDS IS NULL OR BULLETIN.READ_USER_IDS LIKE ? )", "%" + pageIn.get("curUserId", Integer.class) + "%")
			   .addWhere(pageIn.get("curUserId", Integer.class) != 1, "BULLETIN.START_TIME <= ? AND ? <= BULLETIN.END_TIME ", new Date(), new Date())
			   .addWhere(!ValidateUtil.isValid(pageIn.get("state")), "BULLETIN.STATE = 1")
			   .addWhere(ValidateUtil.isValid(pageIn.get("state")), "BULLETIN.STATE = ?",  pageIn.get("state", Integer.class))
			   .addWhere(ValidateUtil.isValid(pageIn.get("showType")), "BULLETIN.SHOW_TYPE = ?", pageIn.get("showType", Integer.class))
			   .addOrder("BULLETIN.SHOW_TYPE", Order.DESC)
			   .addOrder("BULLETIN.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "updateTime", DateUtil.FORMAT_DATE_TIME, "startTime", DateUtil.FORMAT_DATE_TIME,
								"endTime", DateUtil.FORMAT_DATE_TIME);
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
		String sql = "SELECT BULLETIN.ID, BULLETIN.TITLE, BULLETIN.SHOW_TYPE, BULLETIN.UPDATE_TIME, BULLETIN.IMG_FILE_ID, BULLETIN.CONTENT, BULLETIN.STATE, "
				+ "USER.NAME AS UPDATE_USER_NAME, BULLETIN.READ_USER_IDS, BULLETIN.READ_ORG_IDS, BULLETIN.START_TIME, BULLETIN.END_TIME, "
				+ "IFNULL((SELECT GROUP_CONCAT( USER.NAME SEPARATOR ',' ) FROM sys_user USER WHERE USER.state != 0 AND EXISTS ( SELECT 1 FROM EXM_BULLETIN et WHERE BULLETIN.ID = et.ID AND et.READ_USER_IDS LIKE CONCAT( '%,' , USER.ID, ',%' ))),'') AS 'READ_USER_NAMES', "
				+ "IFNULL((SELECT GROUP_CONCAT( ORG.NAME SEPARATOR ',' ) FROM sys_org ORG WHERE ORG.state != 0 AND EXISTS ( SELECT 1 FROM EXM_BULLETIN et WHERE BULLETIN.ID = et.ID AND et.READ_ORG_IDS LIKE CONCAT( '%,' , USER.ID, ',%' ))),'') AS 'READ_ORG_NAMES' "
				+ "FROM EXM_BULLETIN BULLETIN LEFT JOIN SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID WHERE BULLETIN.ID = ? ";
		return getMapList(sql, new Object[]{id});
	}
}