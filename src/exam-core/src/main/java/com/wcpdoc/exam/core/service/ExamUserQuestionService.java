package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.exam.core.entity.ExamUserQuestion;
import com.wcpdoc.exam.core.entity.LoginUser;
/**
 * 考试用户试题服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface ExamUserQuestionService extends BaseService<ExamUserQuestion>{

	/**
	 * 获取考试用户试题列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param examUserId
	 * @return List<ExamUserQuestion>
	 */
	List<ExamUserQuestion> getList(Integer examUserId);

	/**
	 * 删除考试用户试题
	 * 
	 * v1.0 zhanghc 2020年10月12日下午4:03:42
	 * @param examUserId void
	 */
	void delByExamUserId(Integer examUserId);

	/**
	 * 完成阅卷
	 * 
	 * v1.0 zhanghc 2020年10月13日下午1:13:52
	 * @param examId
	 * @param curUser
	 * @param processBarId 
	 * void
	 */
	void doAutoMark(Integer examId, LoginUser curUser, String processBarId);
}
