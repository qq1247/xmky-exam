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
								"COURSE.UPDATE_TIME", "CREATE_USER.NAME AS CREATE_USER_NAME")//
						.leftJoin("SYS_USER CREATE_USER ON COURSE.CREATE_USER_ID = CREATE_USER.ID")//
						.like(pageIn.hasParm("title"), "COURSE.TITLE", pageIn.getParm("title"))//
						.eq(pageIn.hasParm("subAdminUserId"), "EXER.CREATE_USER_ID", pageIn.getParm("subAdminUserId"))// 子管理员登录，各看各的创建的
						.and(pageIn.hasParm("examUserId") && pageIn.hasParm("examOrgId"),
								c -> c.like("EXER.USER_IDS", String.format(",%s,", pageIn.getParm("examUserId"))).or()
										.like("EXER.ORG_IDS", String.format(",%s,", pageIn.getParm("examOrgId"))))// 考试用户看（管理或子管理）分配给自己的
						.eq("COURSE.STATE", 1)//
						.orderByDesc("COURSE.ID"));
		return new PageOut(page.getRecords(), page.getTotal());
	}
}
