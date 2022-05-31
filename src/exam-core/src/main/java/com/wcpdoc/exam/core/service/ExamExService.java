package com.wcpdoc.exam.core.service;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Exam;
/**
 * 考试服务层接口
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
public interface ExamExService extends BaseService<Exam>{
	/**
	 * 发布
	 * 
	 * v1.0 zhanghc 2022年5月30日上午11:28:02
	 * @param exam void
	 */
	void publish(Exam exam);

	/**
	 * 发布
	 * 
	 * v1.0 zhanghc 2022年5月30日上午11:28:08
	 * @param exam void
	 */
	void publishForRand(Exam exam);

	/**
	 * 添加用户
	 * 
	 * v1.0 zhanghc 2022年5月31日上午9:47:52
	 * @param exam
	 * @param examUserIds
	 * @param markUserIds void
	 */
	void userAdd(Exam exam, String[] examUserIds, Integer[] markUserIds);

	/**
	 * 添加用户
	 * 
	 * v1.0 zhanghc 2022年5月31日上午9:51:53
	 * @param exam
	 * @param examUserIds
	 * @param markUserIds void
	 */
	void userAddForRand(Exam exam, String[] examUserIds, Integer[] markUserIds);
}
