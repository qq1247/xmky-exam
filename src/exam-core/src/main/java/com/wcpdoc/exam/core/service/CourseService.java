package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Course;

/**
 * 课程服务层接口
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
public interface CourseService extends BaseService<Course> {

	/**
	 * 课程添加
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course void
	 */
	void add(Course course);

	/**
	 * 课程修改
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course void
	 */
	void update(Course course);

	/**
	 * 课程删除
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course void
	 */
	void del(Integer id);

}
