package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyCourseMaterial;

/**
 * 我的课程资料服务层接口
 * 
 * v1.0 zhanghc 2026-01-23 09:50:50
 */
public interface MyCourseMaterialService extends BaseService<MyCourseMaterial> {

	/**
	 * 我的课程资料生成
	 * 
	 * v1.0 zhanghc 2026-01-23 10:37:31
	 * 
	 * @param courseId void
	 */
	void generate(Integer courseId);

	/**
	 * 我的课程资料列表
	 * 
	 * v1.0 zhanghc 2026-01-23 10:37:31
	 * 
	 * @param courseId void
	 */
	List<MyCourseMaterial> getList(Integer courseId);

	/**
	 * 我的课程资料答案
	 * 
	 * v1.0 zhanghc 2026-01-24 15:45:59
	 * 
	 * @param courseMaterialId
	 * @param questionId
	 * @param userAnswers
	 * @return Boolean
	 */
	Boolean answer(Integer courseMaterialId, Integer questionId, String[] userAnswers);

	/**
	 * 我的课程完成
	 * 
	 * v1.0 zhanghc 2026-01-24 23:00:56
	 * 
	 * @param courseMaterialId void
	 */
	void finish(Integer courseMaterialId);

}
