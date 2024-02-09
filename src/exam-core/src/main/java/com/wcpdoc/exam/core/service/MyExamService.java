package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
/**
 * 我的考试服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamService extends BaseService<MyExam>{

	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 zhanghc 2020年9月29日下午5:09:50
	 * @param examId
	 * @return List<MyExam>
	 */
	List<MyExam> getList(Integer examId);
	
	/**
	 * 获取我的考试列表
	 * 
	 * v1.0 chenyun 2021年7月30日下午3:49:53
	 * @param examId
	 * @param userId
	 * @return MyExam
	 */
	MyExam getMyExam(Integer examId, Integer userId);
	
	/**
	 * 答题
	 * 
	 * v1.0 chenyun 2021年8月24日上午9:55:53
	 * 
	 * v1.1 zhanghc 2021-10-19
	 * 由原参数myQuestionId改成examId questionId userId，从接口层面保证字段的好理解
	 * 
	 * @param examId
	 * @param questionId 
	 * @param userId 
	 * @param answers
	 * void
	 */
	void answerUpdate(Integer examId, Integer userId, Integer questionId, String[] answers);
	
	/**
	 * 完成交卷
	 * 
	 * v1.0 chenyun 2021年8月24日上午10:05:25
	 * v1.1 zhanghc 2021-10-19
	 * 由原参数mymyExamId改成examId questionId userId，从接口层面保证字段的好理解
	 * 
	 * @param examId 
	 * @param userId 
	 * void
	 */
	void finish(Integer examId, Integer userId);
	
	/**
	 * 完成阅卷
	 * 
	 * v1.0 zhanghc 2020年10月13日下午1:13:52
	 * @param examId
	 * void
	 */
	void doExam(Integer examId);

	/**
	 * 完成阅卷
	 * 
	 * v1.0 zhanghc 2020年10月13日下午1:13:52
	 * @param examId
	 * void
	 */
	void doMark(Integer examId);

	/**
	 * 获取考试列表
	 * 
	 * v1.0 zhanghc 2022年7月18日下午2:33:58
	 * @param userId
	 * @return List<Exam>
	 */
	List<Exam> getExamList(Integer userId);

	/**
	 * 我的考试清空
	 * 
	 * v1.0 zhanghc 2023年3月22日下午5:38:23
	 * @param id void
	 */
	void clear(Integer examId);

}
