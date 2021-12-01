package com.wcpdoc.exam.core.service;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.MyExam;
/**
 * 我的考试服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamService extends BaseService<MyExam>{

	/**
	 * 删除实体
	 * 
	 * v1.0 zhanghc 2017年6月26日下午2:09:55
	 * @param examId
	 * @param id
	 * void
	 */
	void del(Integer examId, Integer id);

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
	MyExam getEntity(Integer examId, Integer userId);

	/**
	 * 考试时间表
	 * 
	 * v1.0 chenyun 2021年3月23日上午11:05:51
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> kalendar(Integer year, Integer month);
	
	/**
	 * 成绩排名
	 * 
	 * v1.0 chenyun 2021年3月23日下午3:14:01
	 * @param pageIn
	 * @return PageOut
	 */
	PageOut getRankingPage(PageIn pageIn);
	
	/**
	 * 更新答案
	 * 
	 * v1.0 chenyun 2021年8月24日上午9:55:53
	 * 
	 * v1.1 zhanghc 2021-10-19
	 * 由原参数myExamDetailId改成examId questionId userId，从接口层面保证字段的好理解
	 * 
	 * @param examId
	 * @param questionId 
	 * @param userId 
	 * @param answers
	 * @param answerFileId
	 * void
	 */
	void answerUpdate(Integer examId, Integer userId, Integer questionId, String[] answers, Integer answerFileId);
	
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
	 * 获取用户列表
	 * 
	 * v1.0 zhanghc 2021年10月28日下午1:56:08
	 * @param id
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getUserList(Integer id);
	
	/**
	 * 未阅卷的考试
	 * 
	 * v1.0 chenyun 2021年12月1日上午11:05:27
	 * @param examId
	 * @param markState
	 * @return List<MyExam>
	 */
	List<MyExam> getMarkList(Integer examId, Integer markState);
}
