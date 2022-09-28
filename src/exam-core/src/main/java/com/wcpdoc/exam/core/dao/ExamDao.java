package com.wcpdoc.exam.core.dao;

import java.util.List;
import java.util.Map;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 考试数据访问层接口
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
public interface ExamDao extends BaseDao<Exam>{
	
	/**
	 * 获取考试用户列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	List<Map<String, Object>> getExamUserList(Integer id);
	
	/**
	 * 阅卷考试用户列表
	 * 
	 * v1.0 zhanghc 2021年6月25日下午2:51:40
	 * @param id
	 * @param markUserId
	 * @return List<Map<String,Object>>
	 */
	List<Map<String, Object>> getExamUserList(Integer id, Integer markUserId);
	
	/**
	 * 获取考试分类列表
	 * 
	 * v1.0 chenyun 2022年3月9日下午4:37:56
	 * @return List<Exam>
	 */
	List<Exam> getList();
	
	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年6月6日上午9:18:38
	 * @param id
	 * @return
	 * List<Question>
	 */
	List<Question> getQuestionList(Integer id);
	
	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2022年5月24日下午4:25:35
	 * @param examId
	 * @param userId
	 * @return List<Question>
	 */
	List<Question> getQuestionList(Integer examId, Integer userId);
	
	/**
	 * 获取试题选项列表
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:32:45
	 * @param id
	 * @return List<QuestionOption>
	 */
	List<QuestionOption> getQuestionOptionList(Integer id);
	
	/**
	 * 获取试题选项列表
	 * 
	 * v1.0 zhanghc 2022年5月20日下午1:32:45
	 * @param id
	 * @return List<QuestionOption>
	 */
	List<QuestionOption> getQuestionOptionList(Integer examId, Integer userId);
	
	/**
	 * 获取试题答案分值列表
	 * 
	 * v1.0 chenyun 2022年5月20日下午1:32:45
	 * @param id
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getExamAnswerList(Integer id);
	
	/**
	 * 获取试题答案分值列表
	 * 
	 * v1.0 chenyun 2022年5月20日下午1:32:45
	 * @param examId
	 * @param userId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getExamAnswerList(Integer examId, Integer userId);
}
