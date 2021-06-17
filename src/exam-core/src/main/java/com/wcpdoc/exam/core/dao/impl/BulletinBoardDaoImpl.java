package com.wcpdoc.exam.core.dao.impl;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.BulletinBoardDao;
import com.wcpdoc.exam.core.entity.BulletinBoard;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;

/**
 * 公告栏数据访问层实现
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Repository
public class BulletinBoardDaoImpl extends RBaseDaoImpl<BulletinBoard> implements BulletinBoardDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT * "
				+ "FROM EXM_BULLETIN_BOARD BULLETIN_BOARD ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "BULLETIN_BOARD.ID = ?", pageIn.getTwo())
				;
		PageOut pageOut = getListpage(sqlUtil, pageIn);
				HibernateUtil.formatDate(pageOut.getList(), "UPDATE_TIME", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		String sql = "SELECT USER.ID, USER.NAME AS NAME "
				+ "FROM SYS_USER USER ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "(USER.NAME LIKE ? )", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXISTS (SELECT 1 FROM EXM_BULLETIN_BOARD Z WHERE Z.ID = ? AND Z.READ_USER_IDS LIKE CONCAT('%,', USER.ID, ',%'))", pageIn.getTen())
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
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "(ORG.NAME LIKE ? )", "%" + pageIn.getTwo() + "%")
				.addWhere(ValidateUtil.isValid(pageIn.getTen()), "EXISTS (SELECT 1 FROM EXM_BULLETIN_BOARD Z WHERE Z.ID = ? AND Z.READ_ORG_IDS LIKE CONCAT('%,', ORG.ID, ',%'))", pageIn.getTen())
				.addWhere("ORG.STATE = 1")
				.addOrder("ORG.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		return pageOut;
	}
}