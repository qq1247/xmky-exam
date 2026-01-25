package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.query.MPJQueryWrapper;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.MyCourseQuestion;

/**
 * 我的课程试题数据访问层接口
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
public interface MyCourseQuestionDao extends RBaseDao<MyCourseQuestion> {
	@Override
	default PageOut getListpage(PageIn pageIn) {
		Page<Map<String, Object>> page = selectJoinMapsPage(pageIn.toPage(), //
				new MPJQueryWrapper<MyCourseQuestion>().setAlias("COURSE_MATERIAL")//
						.leftJoin("EXM_COURSE COURSE ON COURSE_MATERIAL.COURSE_ID = COURSE.ID")
						.leftJoin("SYS_USER USER ON COURSE_MATERIAL.UPDATE_USER_ID = USER.ID")
						.select("COURSE_MATERIAL.ID", "COURSE_MATERIAL.NAME", "COURSE_MATERIAL.CONTENT",
								"COURSE_MATERIAL.VIDEO_TIME", "COURSE_MATERIAL.QUESTION_NUM",
								"USER.NAME AS UPDATE_USER_NAME", "COURSE_MATERIAL.NO")//
						.eq(pageIn.hasParm("courseId"), "COURSE_MATERIAL.COURSE_ID", pageIn.getParm("courseId"))//
						.like(pageIn.hasParm("name"), "COURSE_MATERIAL.NAME", pageIn.getParm("name"))//
						.apply(pageIn.hasParm("curUserId"),
								" (COURSE.CREATE_USER_ID = {0} OR COURSE.SHARE_AUTH IN (2, 3)) ",
								pageIn.getParm("curUserId"))//
						.eq("COURSE_MATERIAL.STATE", 1)//
						.orderByDesc("COURSE_MATERIAL.NO"));
		return new PageOut(page.getRecords(), page.getTotal());
	}

	/**
	 * 我的课程试题列表
	 * 
	 * v1.0 zhanghc 2026-01-23 16:23:57
	 * 
	 * @param courseId
	 * @param userId
	 * @return List<MyCourseQuestion>
	 */
	default List<MyCourseQuestion> getList(Integer userId, Integer courseMaterialId) {
		return selectList(new LambdaQueryWrapper<MyCourseQuestion>()//
				.eq(MyCourseQuestion::getUserId, userId)//
				.eq(MyCourseQuestion::getCourseMaterialId, courseMaterialId)//
				.eq(MyCourseQuestion::getState, 1));
	}

	/**
	 * 我的课程试题
	 * 
	 * v1.0 zhanghc 2026-01-23 23:45:28
	 * 
	 * @param courseMaterialId
	 * @param userId
	 * @param questionId
	 * @return MyCourseQuestion
	 */
	default MyCourseQuestion getMyCourseQuestion(Integer userId, Integer courseMaterialId, Integer questionId) {
		return selectOne(new LambdaQueryWrapper<MyCourseQuestion>()//
				.eq(MyCourseQuestion::getUserId, userId)//
				.eq(MyCourseQuestion::getCourseMaterialId, courseMaterialId)//
				.eq(MyCourseQuestion::getQuestionId, questionId)//
				.eq(MyCourseQuestion::getState, 1));
	}

}
