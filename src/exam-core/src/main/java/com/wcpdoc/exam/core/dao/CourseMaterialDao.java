package com.wcpdoc.exam.core.dao;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.CourseMaterial;

/**
 * 课程资料数据访问层接口
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
public interface CourseMaterialDao extends RBaseDao<CourseMaterial> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<CourseMaterial>().setAlias("COURSE_MATERIAL")//
						.leftJoin("EXM_COURSE COURSE ON COURSE_MATERIAL.COURSE_ID = COURSE.ID")
						.leftJoin("SYS_USER USER ON COURSE_MATERIAL.UPDATE_USER_ID = USER.ID")
						.select("COURSE_MATERIAL.ID", "COURSE_MATERIAL.NAME", "COURSE_MATERIAL.CONTENT",
								"COURSE_MATERIAL.VIDEO_SECOND", "COURSE_MATERIAL.QUESTION_NUM",
								"USER.NAME AS UPDATE_USER_NAME")
						.eq(pageIn.hasParm("courseId"), "COURSE_MATERIAL.COURSE_ID",
								pageIn.getParm("courseId"))//
						.like(pageIn.hasParm("name"), "COURSE_MATERIAL.NAME", pageIn.getParm("name"))//
						.apply(pageIn.hasParm("curUserId"),
								" (COURSE.CREATE_USER_ID = {0} OR COURSE.SHARE_AUTH IN (2, 3)) ",
								pageIn.getParm("curUserId"))//
						.eq("COURSE_MATERIAL.STATE", 1)//
						.orderByDesc("COURSE_MATERIAL.UPDATE_TIME"));
		return new PageOut(page.getRecords(), page.getTotal());
	}
}
