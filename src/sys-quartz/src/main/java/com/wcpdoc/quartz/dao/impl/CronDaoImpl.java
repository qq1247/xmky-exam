package com.wcpdoc.quartz.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.wcpdoc.core.dao.impl.RBaseDaoImpl;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.HibernateUtil;
import com.wcpdoc.core.util.SqlUtil;
import com.wcpdoc.core.util.SqlUtil.Order;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.quartz.dao.CronDao;
import com.wcpdoc.quartz.entity.Cron;

/**
 * 定时任务数据访问层实现
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
@Repository
public class CronDaoImpl extends RBaseDaoImpl<Cron> implements CronDao {

	@Override
	public PageOut getListpage(PageIn pageIn) {
		String sql = "SELECT CRON.ID, CRON.NAME, CRON.JOB_CLASS, CRON.CRON, CRON.STATE "
				+ "FROM SYS_CRON CRON "
				+ "LEFT JOIN SYS_USER USER ON CRON.UPDATE_USER_ID = USER.ID ";
		SqlUtil sqlUtil = new SqlUtil(sql);
		sqlUtil.addWhere(ValidateUtil.isValid(pageIn.get("name")), "CRON.NAME LIKE :NAME", "%" + pageIn.get("name") + "%")
				.addOrder("CRON.UPDATE_TIME", Order.DESC);
		PageOut pageOut = getListpage(sqlUtil, pageIn);
		HibernateUtil.formatDate(pageOut.getList(), "UPDATE_TIME", DateUtil.FORMAT_DATE_TIME);
		return pageOut;
	}

	@Override
	public List<Cron> getList() {
		String sql = "SELECT * FROM SYS_CRON";
		return getList(sql);
	}
}