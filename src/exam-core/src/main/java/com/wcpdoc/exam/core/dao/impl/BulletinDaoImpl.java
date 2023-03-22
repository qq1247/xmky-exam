package com.wcpdoc.exam.core.dao.impl;

import java.util.Date;

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
		String sql = "SELECT BULLETIN.ID, BULLETIN.START_TIME, BULLETIN.END_TIME, "
				+ "BULLETIN.TITLE, BULLETIN.CONTENT "
				+ "FROM EXM_BULLETIN BULLETIN "
				+ "LEFT JOIN SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("title")), "BULLETIN.TITLE LIKE :TITLE", "%" + pageIn.get("title") + "%")
				.addWhere("true".equals(pageIn.get("notice")), "BULLETIN.START_TIME <= :START_TIME1 AND :START_TIME2 <= BULLETIN.END_TIME", new Date(), new Date())
				.addWhere("BULLETIN.STATE = 1")
			   .addOrder("BULLETIN.START_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), 
				"startTime", DateUtil.FORMAT_DATE_TIME,
				"endTime", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}
}