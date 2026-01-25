package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyCourseQuestion;

/**
 * 我的课程试题服务层接口
 * 
 * v1.0 zhanghc 2026-01-23 09:50:50
 */
public interface MyCourseQuestionService extends BaseService<MyCourseQuestion> {

	/**
	 * 我的课程试题列表
	 * 
	 * v1.0 zhanghc 2026-01-23 16:23:57
	 * 
	 * @param userId
	 * @param courseMaterialId
	 * @return List<MyCourseQuestion>
	 */
	List<MyCourseQuestion> getList(Integer userId, Integer courseMaterialId);

	/**
	 * 我的课程试题
	 * 
	 * v1.0 zhanghc 2026-01-23 23:44:18
	 * 
	 * @param courseMaterialId
	 * @param questionId
	 * @return MyCourseQuestion
	 */
	MyCourseQuestion getMyCourseQuestion(Integer userId, Integer courseMaterialId, Integer questionId);

}
