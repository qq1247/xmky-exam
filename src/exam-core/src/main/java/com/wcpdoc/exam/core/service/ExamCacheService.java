package com.wcpdoc.exam.core.service;

import java.util.List;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;

/**
 * 考试缓存服务层接口
 * 
 * v1.0 zhanghc 2024年4月14日下午9:36:00
 */
public interface ExamCacheService extends BaseService<Object> {

	/**
	 * 试题信息
	 * 
	 * v1.0 zhanghc 2024年3月12日下午10:00:09
	 * 
	 * @param id
	 * @return Question
	 */
	Question getQuestion(Integer id);

	/**
	 * 试题选项列表
	 * 
	 * v1.0 zhanghc 2024年3月12日下午10:12:09
	 * 
	 * @param questionId
	 * @return List<QuestionOption>
	 */
	List<QuestionOption> getQuestionOptionList(Integer questionId);

	/**
	 * 试题答案列表
	 * 
	 * v1.0 zhanghc 2024年3月12日下午10:19:35
	 * 
	 * @param questionId
	 * @return List<QuestionAnswer>
	 */
	List<QuestionAnswer> getQuestionAnswerList(Integer questionId);

	/**
	 * 考试获取
	 * 
	 * v1.0 zhanghc 2024年3月8日上午11:20:05
	 * 
	 * @param id
	 * @return Exam
	 */
	Exam getExam(Integer id);

	/**
	 * 考试中列表
	 * 
	 * v1.0 zhanghc 2024年3月13日下午4:06:12
	 * 
	 * @param states
	 * @return List<Exam>
	 */
	List<Exam> getExamingList();

	/**
	 * 我的考试
	 * 
	 * v1.0 zhanghc 2024年4月7日下午10:56:08
	 * 
	 * @param examId
	 * @param userId
	 * @return MyExam
	 */
	MyExam getMyExam(Integer examId, Integer userId);

	/**
	 * 我的考试列表
	 * 
	 * v1.0 zhanghc 2024年4月9日下午10:38:13
	 * 
	 * @param examId
	 * @return List<MyExam>
	 */
	List<MyExam> getMyExamList(Integer examId);

	/**
	 * 待阅列表
	 * 
	 * v1.0 zhanghc 2024年3月8日上午10:40:13
	 * 
	 * @return List<MyExam>
	 */
	List<MyExam> getUnMarkList();

	/**
	 * 我的试题列表
	 * 
	 * v1.0 zhanghc 2022年5月20日上午9:18:55
	 * 
	 * @param examId
	 * @param userId
	 * @return List<MyQuestion>
	 */
	List<MyQuestion> getMyQuestionList(Integer examId, Integer userId);

}
