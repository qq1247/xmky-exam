package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.BigDecimalUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperQuestionService;

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
	private PaperQuestionService paperQuestionService;
	@Resource
	private ExamService examService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private PaperServiceImpl paperServiceImpl;
	
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
	public void updateScore(Integer examId, Integer userId, Integer questionId, BigDecimal score) {
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
		
		MyExamDetail myExamDetail = myExamDetailService.getEntity(examId, userId, questionId);
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
		if (curTime - exam.getMarkEndTime().getTime() > 5000) {// 预留5秒网络延时
			throw new MyException("阅卷已结束");
		}
		
		if (exam.getMarkState() != 3) {
			throw new MyException("正在处理智能阅卷部分，请稍后");
		}
		
		List<MyMark> myMarkList = myMarkDao.getList(examId);
		boolean ok = false;
		for (MyMark myMark : myMarkList) {
			if (myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()
					&& myMark.getExamUserIds().contains(String.format(",%s,", userId.toString()))) {
				ok = true;
				break;
			}
		}
		if (!ok) {
			throw new MyException("未参与阅卷");
		}

		if (score != null) {
			PaperQuestion paperQuestion = paperQuestionService.getEntity(exam.getPaperId(), questionId);
			if (BigDecimalUtil.newInstance(score).sub(paperQuestion.getScore()).getResult().doubleValue() > 0) {
				throw new MyException("最大分值：" + paperQuestion.getScore());
			}
		}

		// 更新阅卷分数
		myExamDetail.setScore(score);
		myExamDetail.setMarkUserId(getCurUser().getId());
		myExamDetail.setMarkTime(new Date());
		myExamDetailService.update(myExamDetail);
		
		// 标记为阅卷中，记录阅卷时间
		MyExam myExam = myExamService.getEntity(examId, userId);
		myExam.setMarkState(2);
		if (!ValidateUtil.isValid(myExam.getMarkStartTime())) {
			myExam.setMarkStartTime(new Date());
		} else {
			myExam.setMarkEndTime(new Date());
		}
		myExamService.update(myExam);
	}

	@Override
	public void doScore(Integer examId, Integer userId) {
		// 校验数据有效性
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		
		MyExam myExam = myExamService.getEntity(examId, userId);
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
		if (curTime - exam.getMarkEndTime().getTime() > 5000) {// 预留5秒网络延时
			throw new MyException("阅卷已结束！");
		}
		
		List<MyMark> myMarkList = myMarkDao.getList(examId);
		boolean ok = false;
		for (MyMark myMark : myMarkList) {
			if (myMark.getMarkUserId().intValue() == getCurUser().getId().intValue()
					&& myMark.getExamUserIds().contains(String.format(",%s,", userId.toString()))) {
				ok = true;
				break;
			}
		}
		if (!ok) {
			throw new MyException("未参与阅卷");
		}
		
		List<MyExamDetail> myExamDetailList = myExamDetailService.getList(examId, userId);
		int num = 0;
		BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
		for (MyExamDetail myExamDetail : myExamDetailList) {
			if (myExamDetail.getScore() == null) {
				num++;
			} else {
				totalScore.add(myExamDetail.getScore());
			}
		}
		if (num > 0) {
			throw new MyException("还有" + num + "道题未阅！");
		}
		
		// 标记为已阅，记录阅卷人，统计总分数，标记是否及格
		myExam.setMarkUserId(getCurUser().getId());
		myExam.setMarkEndTime(new Date());
		myExam.setTotalScore(totalScore.getResult());
		myExam.setMarkState(3);
		
		Paper paper = paperServiceImpl.getEntity(exam.getPaperId());
		BigDecimal passScore = BigDecimalUtil.newInstance(paper.getTotalScore()).mul(paper.getPassScore()).div(100, 2).getResult();
		if (BigDecimalUtil.newInstance(totalScore.getResult()).sub(passScore).getResult().doubleValue() > 0) {
			myExam.setAnswerState(1);
		} else {
			myExam.setAnswerState(2);
		}
		myExamService.update(myExam);
	}
}
