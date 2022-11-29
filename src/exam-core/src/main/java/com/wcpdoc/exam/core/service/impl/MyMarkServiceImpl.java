package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.MyQuestionService;

/**
 * 我的阅卷服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyMarkServiceImpl extends BaseServiceImp<Object> implements MyMarkService {
	@Resource
	private MyMarkDao myMarkDao;
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private ExamService examService;
	
	@Override
	public void setDao(BaseDao<Object> dao) {
	}
	
	@Override
	public PageOut getListpage(PageIn pageIn) {
		return myMarkDao.getListpage(pageIn);
	}
	
	@Override
	public PageOut getUserListpage(PageIn pageIn) {
		return myMarkDao.getUserListpage(pageIn);
	}
	
	@Override
	public void scoreUpdate(Integer examId, Integer userId, Integer questionId, BigDecimal userScore, Boolean finish) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
		if (!ValidateUtil.isValid(userScore)) {
			throw new MyException("参数错误：userScore");
		}
		if (!ValidateUtil.isValid(finish)) {
			throw new MyException("参数错误：finish");
		}
		MyExam myExam = myExamService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("未参与考试");
		}
		if (myExam.getMarkUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("未参与考试");
		}
		
		MyQuestion myQuestion = myQuestionService.getMyQuestion(examId, userId, questionId);
		if (myQuestion == null) {
			throw new MyException("未参与考试");
		}
		Exam exam = examService.getEntity(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		long curTime = System.currentTimeMillis();
		if (exam.getMarkStartTime().getTime() > curTime) {
			throw new MyException("阅卷未开始");
		}
		if (curTime - exam.getMarkEndTime().getTime() > 1000) {// 预留1秒网络延时
			throw new MyException("阅卷已结束");
		}
		if (exam.getMarkState() == 1) {
			throw new MyException("正在处理自动阅卷部分，请稍后");
		} 
		if (exam.getMarkState() == 3) {
			throw new MyException("阅卷已结束");
		}
		if (myQuestion.getScore().doubleValue() < userScore.doubleValue()) {
			throw new MyException("最大分值：" + myQuestion.getScore());
		}
		
		// 更新阅卷分数
		myQuestion.setUserScore(userScore);
		myQuestion.setMarkTime(new Date());
		myQuestionService.update(myQuestion);
		
		Date answerStartTime = null, answerEndTime = null, markStartTime = null, markEndTime = null;
		if (finish) {
			List<MyQuestion> myQuestionList = myQuestionService.getList(examId, userId);
			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			for (MyQuestion mq : myQuestionList) {
				if (mq.getUserScore() == null) {
					return;
				}
				totalScore.add(mq.getUserScore());
				
				if (answerStartTime == null) {
					answerStartTime = mq.getAnswerTime();
				} else if (mq.getAnswerTime().getTime() < answerStartTime.getTime()) {
					answerStartTime = mq.getAnswerTime();
				}
				if (answerEndTime == null) {
					answerEndTime = mq.getAnswerTime();
				} else if (mq.getAnswerTime().getTime() > answerEndTime.getTime()) {
					answerEndTime = mq.getAnswerTime();
				}
				if (markStartTime == null) {
					markStartTime = mq.getMarkTime();
				} else if (mq.getMarkTime().getTime() < markStartTime.getTime()) {
					markStartTime = mq.getMarkTime();
				}
				if (markEndTime == null) {
					markEndTime = mq.getMarkTime();
				} else if (mq.getMarkTime().getTime() < markEndTime.getTime()) {
					markEndTime = mq.getMarkTime();
				}
				
			}
			
			
			myExam.setTotalScore(totalScore.getResult());
			myExam.setMarkState(3);
			myExam.setUpdateTime(new Date());
			myExam.setUpdateUserId(getCurUser().getId());
			myExam.setAnswerState(exam.getPassScore().doubleValue() <= myExam.getTotalScore().doubleValue() ? 1 : 2);
			myExam.setAnswerStartTime(answerStartTime);
			myExam.setAnswerEndTime(answerEndTime);
			myExam.setMarkStartTime(markStartTime);
			myExam.setMarkEndTime(markEndTime);
			myExamService.update(myExam);
		}
	}
}
