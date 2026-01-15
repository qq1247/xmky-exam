package com.wcpdoc.exam.core.service;

import java.time.LocalTime;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.CourseMaterial;

/**
 * 课程资料服务层接口
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
public interface CourseMaterialService extends BaseService<CourseMaterial> {

	/**
	 * 课程资料添加
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param courseMaterial
	 * @param answerTimes
	 * @param questionIds    void
	 */
	void add(CourseMaterial courseMaterial, LocalTime[] answerTimes, Integer[] questionIds);

	/**
	 * 课程资料修改
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param courseMaterial
	 * @param answerTimes
	 * @param questionIds    void
	 */
	void update(CourseMaterial courseMaterial, LocalTime[] answerTimes, Integer[] questionIds);

	/**
	 * 课程资料删除
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param id void
	 */
	void del(Integer id);

}
