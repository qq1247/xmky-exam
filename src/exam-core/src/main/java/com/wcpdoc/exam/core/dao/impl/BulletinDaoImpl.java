package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.dao.BulletinDao;
import com.wcpdoc.exam.core.entity.Bulletin;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 公告数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class BulletinDaoImpl extends RBaseDaoImpl<Bulletin> implements BulletinDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT BULLETIN.ID, BULLETIN.TITLE, BULLETIN.TOP_STATE, BULLETIN.UPDATE_TIME, "
				+ "USER.NAME AS UPDATE_USER_NAME, "
				+ "( SELECT GROUP_CONCAT( Z.NAME ) FROM SYS_USER Z WHERE BULLETIN.READ_USER_IDS LIKE CONCAT( '%', Z.ID, '%' ) ) AS READ_USER_NAMES "
				+ "FROM EXM_BULLETIN BULLETIN "
				+ "LEFT JOIN SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("id")), "BULLETIN.ID = ?", pageIn.get("id"))
			   .addWhere(ValidateUtil.isValid(pageIn.get("title")), "BULLETIN.TITLE LIKE ?", "%" + pageIn.get("title") + "%")
			   .addWhere(ValidateUtil.isValid(pageIn.get("topState")), "BULLETIN.TOP_STATE = ?", pageIn.get("topState", Integer.class))
			   .addWhere("BULLETIN.STATE != ?", 0);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "UPDATE_TIME", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), "TOP_STATE", "topState");
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
}