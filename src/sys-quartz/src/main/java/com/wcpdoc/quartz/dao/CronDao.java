package com.wcpdoc.quartz.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.quartz.entity.Cron;

/**
 * 定时任务数据访问层接口
 * 
 * v1.0 zhanghc 2019-07-29 10:38:17
 */
public interface CronDao extends RBaseDao<Cron> {

	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Cron>().setAlias("CRON")//
						.select("CRON.ID", "CRON.NAME", "CRON.JOB_CLASS", "CRON.CRON", "CRON.STATE")//
						.like(pageIn.hasParm("name"), "CRON.NAME", pageIn.getParm("name"))//
						.orderByDesc("CRON.ID"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 获取定时任务列表
	 * 
	 * v1.0 zhanghc 2019年9月13日下午7:10:39
	 * 
	 * @return List<Cron>
	 */
	default List<Cron> getList() {
		return selectList(new LambdaQueryWrapper<Cron>());
	}

}
