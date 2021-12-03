package com.wcpdoc.exam.core.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.wcpdoc.base.cache.DictCache;
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
		String sql = "SELECT BULLETIN.ID, BULLETIN.TITLE, BULLETIN.CONTENT, BULLETIN.SHOW_TYPE, "
				+ "BULLETIN.START_TIME, BULLETIN.END_TIME, BULLETIN.IMG_FILE_ID, "// IMG_FILE_ID首页使用
				+ " BULLETIN.STATE, "
				+ "( SELECT GROUP_CONCAT( Z.NAME ) FROM SYS_USER Z WHERE BULLETIN.READ_USER_IDS LIKE CONCAT( '%,', Z.ID, ',%' ) ) AS READ_USER_NAMES "
				+ "FROM EXM_BULLETIN BULLETIN "
				+ "LEFT JOIN SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("title")), "BULLETIN.TITLE LIKE ?", "%" + pageIn.get("title") + "%")
		.addWhere(ValidateUtil.isValid(pageIn.get("showType")), "BULLETIN.SHOW_TYPE = ?", pageIn.get("showType", Integer.class))
			   .addWhere(pageIn.get("curUserId", Integer.class) == 1, "BULLETIN.UPDATE_USER_ID = ?", pageIn.get("curUserId", Integer.class))// 如果是管理员，查询所有的
			   .addWhere(pageIn.get("curUserId", Integer.class) != 1,
				   	"(BULLETIN.READ_USER_IDS IS NULL OR BULLETIN.READ_USER_IDS LIKE ? ) AND BULLETIN.START_TIME <= ? AND ? <= BULLETIN.END_TIME AND BULLETIN.STATE = 1", 
				   	"%" + pageIn.get("curUserId", Integer.class) + "%", new Date(), new Date())// 如果不是管理员，查询自己有权限查看的，并且在有效时间内，并且是已发布的
			   .addWhere(!ValidateUtil.isValid(pageIn.get("state")), "BULLETIN.STATE IN (1,2)")
			   .addWhere(ValidateUtil.isValid(pageIn.get("state")) && "0".equals(pageIn.get("state")), "BULLETIN.STATE IN (1,2)")
			   .addWhere(ValidateUtil.isValid(pageIn.get("state")) && !"0".equals(pageIn.get("state")), "BULLETIN.STATE = ?", pageIn.get("state"))
			   .addOrder("BULLETIN.SHOW_TYPE", Order.DESC)
			   .addOrder("BULLETIN.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"startTime", DateUtil.FORMAT_DATE_TIME,
				"endTime", DateUtil.FORMAT_DATE_TIME);
		HibernateUtil.formatDict(pageOut.getList(), DictCache.getIndexkeyValueMap(), 
				"BULLETIN_SHOW_TYPE", "showType",
				"EXAM_STATE", "state"
				);
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