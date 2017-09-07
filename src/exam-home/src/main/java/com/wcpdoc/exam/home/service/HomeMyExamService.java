package com.wcpdoc.exam.home.service;

import java.util.List;

import org.springframework.ui.Model;

import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.service.BaseService;
import com.wcpdoc.exam.exam.entity.Exam;
import com.wcpdoc.exam.exam.entity.ExamUser;
import com.wcpdoc.exam.exam.entity.ExamUserQuestion;
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestionEx;
/**
 * 首页服务层接口
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
public interface HomeMyExamService extends BaseService<Object>{

	/**
	 * 获取我的未完成的考试列表
	 * 
	 * v1.0 zhanghc 2017年8月11日下午5:23:46
	 * @param pageIn
	 * @return
	 * PageOut
	 */
	PageOut getMyExamListpage(PageIn pageIn);

	/**
	 * 获取考试信息
	 * 
	 * v1.0 zhanghc 2017年8月14日下午3:45:20
	 * @param examId
	 * @return Exam
	 */
	Exam getExam(Integer examId);

	/**
	 * 获取试卷信息
	 * 
	 * v1.0 zhanghc 2017年8月14日下午3:45:48
	 * @param paperId
	 * @return Paper
	 */
	Paper getPaper(Integer paperId);

	/**
	 * 获取考试用户试题列表
	 * 
	 * v1.0 zhanghc 2017年8月14日下午3:47:05
	 * @param examId
	 * @param userId
	 * @return List<ExamUserQuestion>
	 */
	List<ExamUserQuestion> getExamUserQuestionList(Integer examId, Integer userId);

	/**
	 * 到达试卷页面
	 * 
	 * v1.0 zhanghc 2017年8月28日上午8:59:40
	 * @param model
	 * @param examUserId
	 * @param loginUser 
	 * void
	 */
	void toPaper(Model model, LoginUser loginUser, Integer examUserId);
	
	/**
	 * 更新答案
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param user 
	 * @param examUserQuestionId
	 * @param answer
	 * void
	 */
	void updateAnswer(LoginUser user, Integer examUserQuestionId, String answer);

	/**
	 * 完成试卷
	 * 
	 * v1.0 zhanghc 2017年8月23日上午10:55:25
	 * @param user
	 * @param examUserId
	 * void
	 */
	void doPaper(LoginUser user, Integer examUserId);

	/**
	 * 获取试卷列表
	 * 
	 * v1.0 zhanghc 2017年8月23日上午11:19:37
	 * @param paperId
	 * @return List<PaperQuestionEx>
	 */
	List<PaperQuestionEx> getPaperList(Integer paperId);

	/**
	 * 修改考试用户
	 * 
	 * v1.0 zhanghc 2017年8月23日下午2:04:37
	 * @param examUser
	 * void
	 */
	void updateExamUser(ExamUser examUser);

	/**
	 * 获取考试用户
	 * 
	 * v1.0 zhanghc 2017年8月23日下午2:07:05
	 * @param examId
	 * @param userId
	 * @return ExamUser
	 */
	ExamUser getExamUser(Integer examId, Integer userId);

	/**
	 * 获取考试用户
	 * 
	 * v1.0 zhanghc 2017年8月25日下午5:56:58
	 * @param examUserId
	 * @return ExamUser
	 */
	ExamUser getExamUser(Integer examUserId);
}
