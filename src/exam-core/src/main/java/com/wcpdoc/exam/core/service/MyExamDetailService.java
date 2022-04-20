package com.wcpdoc.exam.core.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.wcpdoc.core.service.BaseService;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.Question;
/**
 * 我的考试详细服务层接口
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
public interface MyExamDetailService extends BaseService<MyExamDetail>{

	/**
	 * 获取我的考试详细列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param examId
	 * @param userId
	 * @return List<MyExamDetail>
	 */
	List<MyExamDetail> getUserAnswerList(Integer examId, Integer userId);

	/**
	 * 获取我的考试详细列表
	 * 
	 * v1.0 zhanghc 2017年7月3日上午9:44:45
	 * @param examId
	 * @param userId
	 * @return List<Map<String, Object>>
	 */
	List<Map<String, Object>> getAnswerList(Integer examId, Integer userId);

	/**
	 * 获取我的考试详细
	 * 
	 * v1.0 zhanghc 2021年10月19日上午9:54:36
	 * @param examId
	 * @param userId
	 * @param questionId
	 * @return MyExamDetail
	 */
	MyExamDetail getEntity(Integer examId, Integer userId, Integer questionId);
	
	/**
	 * 自动阅卷
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
	 * 删除我的考试详细
	 * 
	 * v1.0 zhanghc 2021年10月27日下午2:19:13
	 * @param examId
	 * @param userId void
	 */
	void del(Integer examId, Integer userId);
	
	/**
	 *  获取试题缓存
	 * @return
	 */
	Map<Integer, Question> getQuestionCache(Exam exam, Paper paper);
	
	/**
	 * 获取试题答案缓存
	 * @return
	 */
	Map<Integer, List<PaperQuestionAnswer>> questionAnswerListCache(Integer paperId, Collection<Question> questionList);
	/**
	 * 获取随机试题答案缓存
	 * @return
	 */
	Map<Integer, List<PaperQuestionAnswer>> questionRandAnswerListCache(Integer examId, Integer paperId, Collection<Question> questionRandList);
	/**
	 * 获取试题选项缓存
	 * @return
	 */
	Map<Integer, PaperQuestion> questionOptionCache(Integer paperId);
	/**
	 * 获取随机试题选项缓存
	 * @return
	 */
	Map<Integer, PaperQuestion> questionRandOptionCache(Integer examId, Integer paperId);
	/**
	 * 客观题阅卷
	 * 
	 * @return
	 */
	void markerUser(Exam exam, Paper paper, Map<Integer, Question> questionCache, Map<Integer, List<PaperQuestionAnswer>> questionAnswerListCache, Map<Integer, PaperQuestion> questionOptionCache, MyExam myExam);
}
