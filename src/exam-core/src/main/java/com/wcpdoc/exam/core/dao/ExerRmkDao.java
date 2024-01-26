package com.wcpdoc.exam.core.dao;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.ExerRmk;

/**
 * 练习评论
 * 
 * v1.0 chenyun 2021年8月31日上午9:59:36
 */
public interface ExerRmkDao extends RBaseDao<ExerRmk> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<ExerRmk>().setAlias("EXER_RMK")//
						.leftJoin("SYS_USER USER ON USER.ID = EXER_RMK.UPDATE_USER_ID")
						.select("EXER_RMK.ID", "EXER_RMK.CONTENT", "EXER_RMK.LIKE_NUM", "EXER_RMK.LIKE_USER_IDS", // LIKE_USER_IDS用于当前用户已点赞时，选中点赞图标
								"EXER_RMK.UPDATE_USER_ID", "USER.NAME AS UPDATE_USER_NAME", // UPDATE_USER_ID,
								"EXER_RMK.UPDATE_TIME")// UPDATE_USER_NAME，页面显示是谁，没有就是匿名
						.eq(pageIn.hasParm("questionId"), "EXER_RMK.QUESTION_ID", pageIn.getParm("questionId"))//
						.eq("EXER_RMK.STATE", 1)// 只看有效数据
						.orderByDesc("EXER_RMK.LIKE_NUM", "EXER_RMK.UPDATE_TIME"));// 按评论数倒序，在按更新时间倒序
		return new PageOut(page.getRecords(), page.getTotal());
	}
}
