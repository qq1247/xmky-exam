package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.CourseQuestion;

/**
 * 课程试题服务层接口
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
public interface CourseQuestionService extends BaseService<CourseQuestion> {

	/**
	 * 课程试题列表
	 * 
	 * v1.0 zhanghc 2026年1月13日21:51:39
	 * 
	 * @param courseMaterialId
	 * @return List<CourseQuestion>
	 */
	List<CourseQuestion> getList(Integer courseMaterialId);

}
