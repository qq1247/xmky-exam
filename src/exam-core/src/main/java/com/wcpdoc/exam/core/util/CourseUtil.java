package com.wcpdoc.exam.core.util;

import com.wcpdoc.exam.core.entity.Course;

/**
 * 课程工具类
 * 
 * v1.0 zhanghc 2026-01-12 10:03:53
 */
public class CourseUtil {
	/**
	 * 是否私有权限
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course
	 * @return boolean
	 */
	public static boolean hasPrivate(Course course) {
		return course.getShareAuth() == 1;
	}

	/**
	 * 是否有读权限
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course
	 * @return boolean
	 */
	public static boolean hasRead(Course course) {
		return course.getShareAuth() == 2 || course.getShareAuth() == 3;
	}

	/**
	 * 是否有写权限
	 * 
	 * v1.0 zhanghc 2026-01-12 10:03:53
	 * 
	 * @param course
	 * @return boolean
	 */
	public static boolean hasWrite(Course course) {
		return course.getShareAuth() == 3;
	}
}
