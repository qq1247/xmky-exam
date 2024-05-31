package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.ex.PaperPart;

/**
 * 我的考试服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamService extends BaseService<MyExam> {
	/**
	 * 试卷
	 * 
	 * v1.0 zhanghc 2024年5月21日下午12:19:17
	 * 
	 * @param examId
	 * @param userId
	 * @return List<PaperPart>
	 */
	List<PaperPart> paper(Integer examId, Integer userId);

	/**
	 * 试卷处理（主要用途是注解缓存，必须有接口才可以）
	 * 
	 * v1.0 zhanghc 2024年5月21日下午12:19:25
	 * 
	 * @param examId
	 * @param userId void
	 */
	void paperHandle(Integer examId, Integer userId);

	/**
	 * 答题
	 * 
	 * v1.0 chenyun 2021年8月24日上午9:55:53
	 * 
	 * v1.1 zhanghc 2021-10-19 由原参数myQuestionId改成examId questionId
	 * userId，从接口层面保证字段的好理解
	 * 
	 * @param examId
	 * @param questionId
	 * @param userId
	 * @param answers    void
	 */
	void answer(Integer examId, Integer userId, Integer questionId, String[] answers);

	/**
	 * 交卷
	 * 
	 * v1.0 chenyun 2021年8月24日上午10:05:25 v1.1 zhanghc 2021-10-19
	 * 由原参数mymyExamId改成examId questionId userId，从接口层面保证字段的好理解
	 * 
	 * @param examId
	 * @param userId void
	 */
	void finish(Integer examId, Integer userId);

	/**
	 * 我的考试清理
	 * 
	 * v1.0 zhanghc 2023年3月22日下午5:38:23
	 * 
	 * @param id void
	 */
	void clear(Integer examId);
}
