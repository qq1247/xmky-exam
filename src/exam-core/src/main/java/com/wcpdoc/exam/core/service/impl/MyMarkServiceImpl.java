package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.MyMarkDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.util.BigDecimalUtil;

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
	public void updateScore(Integer myExamDetailId, BigDecimal score) {
		// 校验数据有效性
		if (myExamDetailId == null ) {
			throw new MyException("参数错误：myExamDetailId");
		}
		if (score == null) {
			throw new MyException("参数错误：score");
		}
		
		MyExamDetail myExamDetail = myExamDetailService.getEntity(myExamDetailId);
		List<MyMark> myMarkList = myMarkDao.getList(myExamDetail.getExamId());
		Exam exam = examService.getEntity(myExamDetail.getExamId());
		if (exam.getState() == 0) {
			throw new MyException("考试已删除！");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布！");
		}
		if (exam.getMarkStartTime().getTime() > (new Date().getTime())) {
			throw new MyException("阅卷未开始！");
		}
		if (exam.getMarkEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
			throw new MyException("阅卷已结束！");
		}

		boolean ok = false;
		for (MyMark myMark : myMarkList) {
			if (myMark.getMarkUserId() == getCurUser().getId()) {
				ok = true;
				break;
			}
		}

		if (!ok) {
			throw new MyException("未参与考试：" + exam.getName());
		}

		if (score != null) {
			PaperQuestion paperQuestion = paperQuestionService.getEntity(exam.getPaperId(), myExamDetail.getQuestionId());
			if (BigDecimalUtil.newInstance(score).sub(paperQuestion.getScore()).getResult().doubleValue() > 0) {
				throw new MyException("最大分值：" + paperQuestion.getScore());
			}
		}

		// 更新阅卷分数
		myExamDetail.setScore(score);
		myExamDetail.setMyMarkId(getCurUser().getId());
		myExamDetail.setMarkTime(new Date());
		myExamDetailService.update(myExamDetail);
		
		//我的考试更新分数
		BigDecimal totalScore = new BigDecimal(0);
		List<MyExamDetail> MyExamDetailList = myExamDetailService.getList(myExamDetail.getMyExamId());
		for(MyExamDetail entity : MyExamDetailList){
			if (entity.getScore() == null) {
				entity.setScore(new BigDecimal(0));
			}
			totalScore = totalScore.add(entity.getScore());
		}
		MyExam myExam = myExamService.getEntity(myExamDetail.getMyExamId());
		myExam.setAnswerEndTime(new Date());
		myExam.setTotalScore(totalScore);
		myExamService.update(myExam);
	}

	@Override
	public void doScore(Integer examId, Integer userId, Integer markId) {
		// 校验数据有效性
		//MyExam myExam = myExamService.getEntity(markId);
		MyMark myMark = myMarkDao.getEntity(markId);
		Exam exam = examService.getEntity(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除！");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试未发布！");
		}
		if (exam.getMarkStartTime().getTime() > (new Date().getTime())) {
			throw new MyException("阅卷未开始！");
		}
		if (exam.getMarkEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
			throw new MyException("阅卷已结束！");
		}

		if (myMark.getMarkUserId() != getCurUser().getId()) {
			throw new MyException("未参与阅卷：" + exam.getName());
		}
		MyExam myExam = myExamService.getEntity(examId, userId);
		List<MyExamDetail> myExamDetailList = myExamDetailService.getList(myExam.getId());
		int num = 0;
		BigDecimal totalScore = new BigDecimal(0);
		for (MyExamDetail myExamDetail : myExamDetailList) {
			if (myExamDetail.getScore() == null) {
				num++;
			} else {
				totalScore = BigDecimalUtil.newInstance(myExamDetail.getScore()).add(totalScore).getResult();
			}
		}

		if (num > 0) {
			throw new MyException("还有" + num + "道题未阅！");
		}

		// 标记为已阅
		myExam.setMarkState(3);
		myExam.setMyMarkId(getCurUser().getId());
		myExam.setMarkEndTime(new Date());
		myExam.setTotalScore(totalScore);
		Paper paper = paperServiceImpl.getEntity(exam.getPaperId());
		BigDecimal divide = paper.getTotalScore().multiply(paper.getPassScore()).divide(new BigDecimal(100));
		if (totalScore.compareTo(divide) == 1) {
			myExam.setAnswerState(1);
		} else {
			myExam.setAnswerState(2);
		}
		myExam.setUpdateTime(new Date());
		myExam.setUpdateUserId(getCurUser().getId());
		myExamService.update(myExam);
	}
}
