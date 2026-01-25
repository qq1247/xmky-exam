package com.wcpdoc.exam.core.dao;

import java.util.Map;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.Course;

/**
 * 课程数据访问层接口
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
public interface CourseDao extends RBaseDao<Course> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<Course>().setAlias("COURSE")//
						.select("COURSE.ID", "COURSE.NAME", "COURSE.CONTENT", "COURSE.SHARE_AUTH", "COURSE.STATE",
								"COURSE.UPDATE_TIME", "CREATE_USER.NAME AS CREATE_USER_NAME", "COURSE.ORG_IDS",
								"COURSE.USER_IDS",
								"(SELECT COUNT(*) FROM EXM_COURSE_MATERIAL Z WHERE COURSE.ID = Z.COURSE_ID AND Z.STATE = 1) AS COURSE_MATERIAL_NUM",
								"COALESCE((SELECT SUM(QUESTION_NUM) FROM EXM_COURSE_MATERIAL Z WHERE COURSE.ID = Z.COURSE_ID AND Z.STATE = 1), 0) AS QUESTION_NUM")
						.leftJoin("SYS_USER CREATE_USER ON COURSE.CREATE_USER_ID = CREATE_USER.ID")//
						.eq(pageIn.hasParm("id"), "COURSE.ID", pageIn.getParm("id"))// 
						.like(pageIn.hasParm("name"), "COURSE.NAME", pageIn.getParm("name"))//
						.apply(pageIn.hasParm("subAdminUserId"),
								" (COURSE.CREATE_USER_ID = {0} OR COURSE.SHARE_AUTH IN (2, 3)) ",
								pageIn.getParm("subAdminUserId"))// 子管理员登录，看自己创建的和有共享权限的
						.and(pageIn.hasParm("examUserId") && pageIn.hasParm("examOrgId"),
								c -> c.like("COURSE.USER_IDS", String.format(",%s,", pageIn.getParm("examUserId"))).or()
										.like("COURSE.ORG_IDS", String.format(",%s,", pageIn.getParm("examOrgId"))))// 考试用户看（管理或子管理）分配给自己的
						.eq(pageIn.hasParm("state"), "COURSE.STATE", pageIn.getParm("state"))//
						.in("COURSE.STATE", 1, 2)//
						.orderByDesc("COURSE.ID"));
		return new PageOut(page.getRecords(), page.getTotal());
	}
}
