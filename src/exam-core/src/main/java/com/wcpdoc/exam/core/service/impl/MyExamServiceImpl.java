package com.wcpdoc.exam.core.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.file.service.FileService;

/**
 * 我的考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyExamServiceImpl extends BaseServiceImp<MyExam> implements MyExamService {
	
	@Resource
	private MyExamDao myExamDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private ExamService examService;
	@Resource
	private MyExamDetailService myExamDetailService;
	@Resource
	private FileService fileService;

	@Override
	@Resource(name = "myExamDaoImpl")
	public void setDao(BaseDao<MyExam> dao) {
		super.dao = dao;
	}

	@Override
	public void del(Integer examId, Integer userId) {
		myExamDao.del(examId, userId);
	}

	@Override
	public List<MyExam> getList(Integer examId) {
		return myExamDao.getList(examId);
	}
	
	@Override
	public MyExam getEntity(Integer examId, Integer userId) {
		return myExamDao.getEntity(examId, userId);
	}
	
	@Override
	public List<Map<String, Object>> kalendar(Integer year, Integer month) {
		String time = year+"-"+month+"-01 00:00:00";
		Date dateTime = DateUtil.getDateTime(time);
		List<MyExam> myExamList = myExamDao.kalendar(getCurUser().getId(), DateUtil.getMonthStart(dateTime), DateUtil.getMonthEnd(dateTime));
		if(myExamList == null){
			return null;			
		}
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for(MyExam myExam : myExamList){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", myExam.getId());
			map.put("examId", myExam.getExamId());
			map.put("time", DateUtil.formatDateTime(myExam.getAnswerStartTime()));
			list.add(map);
		}
		return list;
	}

	@Override
	public PageOut getRankingPage(PageIn pageIn) {
		return myExamDao.getRankingPage(pageIn);
	}

	@Override
	public void answerUpdate(Integer examId, Integer userId, Integer questionId, String[] answers, Integer answerFileId) {
		// 校验数据有效性
		if (examId == null) {
			throw new MyException("参数错误：examId");
		}
		if (userId == null) {
			throw new MyException("参数错误：userId");
		}
		if (questionId == null) {
			throw new MyException("参数错误：questionId");
		}

		// if(!ValidateUtil.isValid(answer)) {
		// throw new MyException("参数错误：answer");
		// }//如是多选取消勾选则为空

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
		if (exam.getStartTime().getTime() > curTime) {
			throw new MyException("考试未开始");
		}
		if (curTime - exam.getEndTime().getTime() > 1000) {// 预留1秒网络延时
			throw new MyException("考试已结束");
		}

		// 标记为考试中，记录答题时间
		MyExam myExam = getEntity(examId, userId);
		myExam.setState(2);
		if (!ValidateUtil.isValid(myExam.getAnswerStartTime())) {
			myExam.setAnswerStartTime(new Date());
			myExam.setAnswerEndTime(new Date());// 如果只答一道题，这里不加，结束时间就是空
		} else {
			myExam.setAnswerEndTime(new Date());
		}
		update(myExam);
		
		// 保存答案
		Question question = questionService.getEntity(questionId);
		if (!ValidateUtil.isValid(answers)) {
			myExamDetail.setAnswer(null);
		} else if (question.getType() == 1 || question.getType() == 4 || question.getType() == 5) {
			myExamDetail.setAnswer(answers[0]);
		} else if (question.getType() == 2) {
			Arrays.sort(answers);// 页面先选d在选c，值为db，这里重新排序一下
			myExamDetail.setAnswer(StringUtil.join(answers));
		} else if (question.getType() == 3) {
			myExamDetail.setAnswer(StringUtil.join(answers, '\n'));
		}
		if (ValidateUtil.isValid(answerFileId)) {
			myExamDetail.setAnswerFileId(answerFileId);
		}
		myExamDetail.setAnswerTime(new Date());
		myExamDetailService.update(myExamDetail);
		
		// 保存附件
		if (ValidateUtil.isValid(answerFileId)) {
			fileService.doUpload(answerFileId);
		}
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
		MyExam myExam = getEntity(examId, userId);
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
		if (exam.getStartTime().getTime() > curTime) {
			throw new MyException("考试未开始");
		}
		if (curTime - exam.getEndTime().getTime() > 1000) {// 预留1秒网络延时
			throw new MyException("考试已结束");
		}
	
		// 标记为已交卷，记录最后交卷时间
		myExam.setState(3);
		myExam.setAnswerEndTime(new Date());
		update(myExam);
	}

	@Override
	public List<Map<String, Object>> getUserList(Integer examId) {
		return myExamDao.getUserList(examId);
	}
}
