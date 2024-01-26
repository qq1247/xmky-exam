package com.wcpdoc.exam.core.dao;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Bulletin;

/**
 * 公告数据访问层接口
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
public interface BulletinDao extends RBaseDao<Bulletin> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Bulletin>().setAlias("BULLETIN")//
						.select("BULLETIN.ID", "BULLETIN.START_TIME", "BULLETIN.END_TIME", "BULLETIN.TITLE",
								"BULLETIN.CONTENT")//
						.leftJoin("SYS_USER USER ON BULLETIN.UPDATE_USER_ID = USER.ID")//
						.like(pageIn.hasParm("title"), "BULLETIN.TITLE", pageIn.getParm("title"))//
						.eq("BULLETIN.STATE", 1)//
						.orderByDesc("BULLETIN.START_TIME"));
		return new PageOut(page.getRecords(), page.getTotal());
	}
}
