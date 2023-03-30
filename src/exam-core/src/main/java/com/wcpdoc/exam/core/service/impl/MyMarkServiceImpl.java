package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Collections;
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
import com.wcpdoc.exam.core.cache.QuestionCache;
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
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
public class MyMarkServiceImpl extends BaseServiceImp<MyMark> implements MyMarkService {
	@Resource
	private MyMarkDao myMarkDao;
	@Resource
	private MyExamService myExamService;
	@Resource
	private ExamService examService;
	@Resource
	private MyQuestionService myQuestionService;

	@Override
	@Resource(name = "myMarkDaoImpl")
	public void setDao(BaseDao<MyMark> dao) {
		super.dao = dao;
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
	public List<MyMark> getList(Integer examId) {
		return myMarkDao.getList(examId);
	}

	@Override
	public void assign(Integer examId, Integer num) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(num) || num < 1 || num > 100) {
			throw new MyException("参数错误：num");
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
		if (curTime - exam.getMarkEndTime().getTime() > 0) {
			throw new MyException("阅卷已结束");
		}
		if (exam.getMarkState() == 1) {
			throw new MyException("客观题正在阅卷中，请等待");
		}
		if (exam.getMarkState() == 3) {
			throw new MyException("阅卷已结束");
		}
		
		// 随机分配试卷
		List<MyExam> myExamList = myExamService.getList(examId);
		Collections.shuffle(myExamList);
		for (MyExam myExam : myExamList) {
			if (!ValidateUtil.isValid(myExam.getMarkUserId())) {
				myExam.setMarkUserId(getCurUser().getId());
				myExamService.update(myExam);
				
				if (--num < 1) {
					break;
				}
			}
		}
	}
	
	@Override
	public void scoreUpdate(Integer examId, Integer userId, Integer questionId, BigDecimal userScore) {
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
		if (exam.getMarkType() == 1) {
			throw new MyException("无需人工阅卷");
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
		if (userScore.doubleValue() > myQuestion.getScore().doubleValue()) {
			throw new MyException("最大分值：" + myQuestion.getScore());
		}
		if (userScore.doubleValue() < 0) {
			throw new MyException("最小分值：0");
		}
		Question question = QuestionCache.getQuestion(questionId);// 已关联考试的试题不会改变，缓存起来加速查询。
		if (question.getMarkType() != 2) {
			throw new MyException("该题为客观题");
		}
		if (myExam.getState() == 1) {
			throw new MyException("未参与考试，阅卷无效");
		}
		
		// 打分
		if (!ValidateUtil.isValid(myExam.getMarkStartTime())) {
			myExam.setMarkStartTime(new Date());
			myExam.setMarkEndTime(new Date());// 如果只阅一道题，这里不加，结束时间就是空
		} else {
			myExam.setMarkEndTime(new Date());
		}
		myQuestion.setUserScore(userScore);
		myQuestion.setMarkTime(new Date());
		myQuestionService.update(myQuestion);
	}

	@Override
	public void finish(Integer examId, Integer userId) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		MyExam myExam = myExamService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("未参与考试");
		}
		if (myExam.getMarkUserId().intValue() != getCurUser().getId().intValue()) {
			throw new MyException("未参与考试");
		}
		
		Exam exam = examService.getEntity(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		if (exam.getMarkType() == 1) {
			throw new MyException("无需人工阅卷");
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
		List<MyQuestion> myQuestionList = myQuestionService.getList(examId, userId);
		int unMarkNum = 0;
		for (MyQuestion myQuestion : myQuestionList) {
			if (myQuestion.getType() == 2 && myQuestion.getUserScore() == null) {
				unMarkNum++;
			}
		}
		if (unMarkNum > 0) {
			throw new MyException(String.format("剩余%s道未阅", unMarkNum));
		}
		
		// 阅卷
		BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
		for (MyQuestion myQuestion : myQuestionList) {
			if (myQuestion.getType() == 2) {
				totalScore.add(myQuestion.getUserScore());
			}
		}
		
		myExam.setTotalScore(totalScore.getResult());
		myExam.setMarkState(3);
		myExam.setAnswerState(exam.getPassScore().doubleValue() <= myExam.getTotalScore().doubleValue() ? 1 : 2);
		myExamService.update(myExam);
	}
}
