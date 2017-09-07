package com.wcpdoc.exam.home.service;

import java.math.BigDecimal;
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
 * 判卷服务层接口
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
public interface HomeMarkService extends BaseService<Object>{

	/**
	 * 获取判卷列表
	 * 
	 * v1.0 zhanghc 2017年8月11日下午5:23:46
	 * @param pageIn
	 * @return
	 * PageOut
	 */
	PageOut getMarkPaperListpage(PageIn pageIn);

	/**
	 * 获取考试用户
	 * 
	 * v1.0 zhanghc 2017年8月23日下午4:33:20
	 * @param examUserId
	 * @return ExamUser
	 */
	ExamUser getExamUser(Integer examUserId);

	/**
	 * 获取考试
	 * 
	 * v1.0 zhanghc 2017年8月23日下午4:35:41
	 * @param examId
	 * @return Exam
	 */
	Exam getExam(Integer examId);

	/**
	 * 获取试卷
	 * 
	 * v1.0 zhanghc 2017年8月23日下午4:47:21
	 * @param paperId
	 * @return Paper
	 */
	Paper getPaper(Integer paperId);

	/**
	 * 修改考试用户
	 * 
	 * v1.0 zhanghc 2017年8月23日下午4:49:25
	 * @param examUser
	 * void
	 */
	void updateExamUser(ExamUser examUser);

	/**
	 * 获取试题列表
	 * 
	 * v1.0 zhanghc 2017年8月23日下午4:52:28
	 * @param paperId
	 * @return List<PaperQuestionEx>
	 */
	List<PaperQuestionEx> getPaperList(Integer paperId);

	/**
	 * 获取考试用户试题列表
	 * 
	 * v1.0 zhanghc 2017年8月23日下午5:40:21
	 * @param examId
	 * @param userId
	 * @return List<ExamUserQuestion>
	 */
	List<ExamUserQuestion> getExamUserQuestionList(Integer examId, Integer userId);
	
	/**
	 * 到达判卷页面
	 * 
	 * v1.0 zhanghc 2017年8月28日上午11:04:18
	 * @param model
	 * @param user 
	 * @param examUserId
	 * void
	 */
	void toMark(Model model, LoginUser user, Integer examUserId);

	/**
	 * 更新判卷分数
	 * 
	 * v1.0 zhanghc 2017年8月23日下午6:41:17
	 * @param user 
	 * @param examUserQuestionId
	 * @param score
	 * void
	 */
	void updateScore(LoginUser user, Integer examUserQuestionId, BigDecimal score);

	/**
	 * 完成判卷
	 * 
	 * v1.0 zhanghc 2017年8月23日下午6:42:32
	 * @param user 
	 * @param examUserId
	 * void
	 */
	void doMark(LoginUser user, Integer examUserId);

	/**
	 * 到达判卷预览页面
	 * 
	 * v1.0 zhanghc 2017年8月28日下午4:55:42
	 * @param user
	 * @param model
	 * @param examUserId
	 * void
	 */
	void toMarkView(LoginUser user, Model model, Integer examUserId);
}
