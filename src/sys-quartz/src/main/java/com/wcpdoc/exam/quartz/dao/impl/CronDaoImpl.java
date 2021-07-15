package com.wcpdoc.exam.quartz.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.exam.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.HibernateUtil;
import com.wcpdoc.exam.core.util.SqlUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;
import com.wcpdoc.exam.core.util.SqlUtil.Order;
import com.wcpdoc.exam.quartz.dao.CronDao;
import com.wcpdoc.exam.quartz.entity.Cron;

/**
 * 定时任务数据访问层实现
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
@Repository
public class CronDaoImpl extends RBaseDaoImpl<Cron> implements CronDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT CRON.ID, CRON.NAME, CRON.JOB_CLASS, CRON.CRON, CRON.STATE, USER.NAME AS UPDATE_USER_NAME, CRON.UPDATE_TIME "
				+ "FROM SYS_CRON CRON "
				+ "LEFT JOIN SYS_USER USER ON CRON.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.getTwo()), "CRON.NAME LIKE ?", "%" + pageIn.getTwo() + "%")
				.addOrder("CRON.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getRows(), "UPDATE_TIME", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<Cron> getList() {
		String sql = "SELECT * FROM SYS_CRON";
		return getList(sql);
	}
}