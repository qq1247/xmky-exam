package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;

/**
 * 我的阅卷服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyMarkServiceImpl extends BaseServiceImp<MyMark> implements MyMarkService {
	private static final Logger log = LoggerFactory.getLogger(MyMarkServiceImpl.class);
	@Resource
	private MyMarkDao myMarkDao;
	@Resource
	private MyExamService myExamService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private ExamService examService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private UserService userService;
	
	@Override
	@Resource(name = "myMarkDaoImpl")
	public void setDao(BaseDao<MyMark> dao) {
		super.dao = dao;
	}

	@Override
	public List<MyMark> getList(Integer examId) {
		return myMarkDao.getList(examId);
	}
	
	@Override
	public void scoreUpdate(Integer examId, Integer userId, Integer questionId, BigDecimal score) {
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
		if (!ValidateUtil.isValid(score)) {
			throw new MyException("参数错误：score");
		}
		
		MyExamDetail myExamDetail = myExamDetailService.getMyExamDetail(examId, userId, questionId);
		if (myExamDetail == null) {
			throw new MyException("未参与考试");
		}
		Exam exam = examService.getEntity(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布");
		}
		long curTime = System.currentTimeMillis();
		if (exam.getMarkStartTime().getTime() > curTime) {
			throw new MyException("阅卷未开始");
		}
		if (curTime - exam.getMarkEndTime().getTime() > 1000) {// 预留1秒网络延时
			throw new MyException("阅卷已结束");
		}
		if (exam.getMarkState() == 1) {
			throw new MyException("正在处理智能阅卷部分，请稍后");
		}
		if (exam.getMarkState() == 3) {
			throw new MyException("阅卷已结束");
		}
		
		MyExam myExam = myExamService.getMyExam(examId, userId);
		if (myExam.getState() == 1) {
			throw new MyException("用户未考试");
		}
		
		List<MyMark> myMarkList = myMarkDao.getList(examId);
		boolean hasMark = false;
		for (MyMark myMark : myMarkList) {
			if (myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()
					&& myMark.getExamUserIds().contains(String.format(",%s,", userId.toString()))) {
				hasMark = true;
				break;
			}
		}
		if (!hasMark) {
			throw new MyException("未参与阅卷");
		}

		ExamQuestion examQuestion;
		if (exam.getGenType() == 1) {
			examQuestion = examQuestionService.getEntity(exam.getId(), questionId);
		} else {
			examQuestion = examQuestionService.getEntity(exam.getId(), userId, questionId);
		}
		if (BigDecimalUtil.newInstance(score).sub(examQuestion.getScore()).getResult().doubleValue() > 0) {
			throw new MyException("最大分值：" + examQuestion.getScore());
		}

		// 更新阅卷分数
		myExamDetail.setScore(score);
		myExamDetail.setMarkUserId(getCurUser().getId());
		myExamDetail.setMarkTime(new Date());
		myExamDetailService.update(myExamDetail);
		
		// 标记为阅卷中，记录阅卷时间
		myExam.setMarkState(2); // 只要打分就标记为阅卷中，可能的问题为前端调用了交卷方法，然后调用该方法时，交卷方法调用失败。这个给分了，阅卷时间到，发现交卷就不在处理，导致结果错误
		if (!ValidateUtil.isValid(myExam.getMarkStartTime())) {
			myExam.setMarkStartTime(new Date());
			myExam.setMarkEndTime(new Date());//如果只阅一道题，就没有结束时间。
		} else {
			myExam.setMarkEndTime(new Date());
		}
		myExam.setMarkUserId(getCurUser().getId());// 一张试卷可能多个人阅卷（按题阅卷），更新成最后一个人。
		myExamService.update(myExam);
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
		Exam exam = examService.getEntity(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布");
		}
		long curTime = System.currentTimeMillis();
		if (exam.getMarkStartTime().getTime() > curTime) {
			throw new MyException("阅卷未开始");
		}
		if (curTime - exam.getMarkEndTime().getTime() > 1000) {// 预留1秒网络延时
			throw new MyException("阅卷已结束");
		}
		
		List<MyMark> myMarkList = myMarkDao.getList(examId);
		boolean hasMark = false;
		for (MyMark myMark : myMarkList) {
			if (myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()
					&& myMark.getExamUserIds().contains(String.format(",%s,", userId.toString()))) {
				hasMark = true;// 多人阅卷情况下，有一个符合就可以
				break;
			}
		}
		if (!hasMark) {
			throw new MyException("未参与阅卷");
		}
		
		// 如果还有未阅卷的题，不交卷
		List<MyExamDetail> userAnswerList = myExamDetailService.getList(examId, userId);
		BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
		User examUser = userService.getEntity(userId);
		User markUser = userService.getEntity(getCurUser().getId());
		for (MyExamDetail userAnswer : userAnswerList) {
			if (userAnswer.getScore() == null) {// 如果按题阅卷或多人阅一张试卷，交卷时应该等待所有题都阅卷。阅卷时间到也会做最终的处理。
				log.info("主观题交卷：【{}-{}】【{}-{} 阅 {}-{}】，编号为{}的试题未阅卷，等待完成", exam.getId(), exam.getName(), 
						markUser.getId(), markUser.getName(), examUser.getId(), examUser.getName(), userAnswer.getQuestionId());
				return;
			}
			totalScore.add(userAnswer.getScore());
		}
		
		// 标记为已阅，记录阅卷人，统计总分数，标记是否及格
		myExam.setMarkUserId(getCurUser().getId());
		myExam.setMarkEndTime(new Date());
		myExam.setTotalScore(totalScore.getResult());
		myExam.setMarkState(3);
		
		BigDecimal passScore = BigDecimalUtil.newInstance(exam.getTotalScore()).mul(exam.getPassScore()).div(100, 2).getResult();
		if (BigDecimalUtil.newInstance(totalScore.getResult()).sub(passScore).getResult().doubleValue() >= 0) {
			myExam.setAnswerState(1);
		} else {
			myExam.setAnswerState(2);
		}
		myExamService.update(myExam);
		log.info("主观题交卷：【{}-{}】【{}-{} 阅 {}-{}】，得{}分，{}", exam.getId(), exam.getName(), 
				markUser.getId(), markUser.getName(), examUser.getId(), examUser.getName(), 
				myExam.getTotalScore(), myExam.getAnswerState() == 1 ? "及格" : "不及格");
	}

	@Override
	public List<MyMark> getListForMarkUser(Integer markUserId) {
		return myMarkDao.getListForMarkUser(markUserId);
	}
}
