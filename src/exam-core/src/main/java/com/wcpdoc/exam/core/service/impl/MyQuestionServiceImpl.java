package com.wcpdoc.exam.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;

import com.wcpdoc.core.dao.BaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.exam.core.dao.MyQuestionDao;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyQuestionService;

/**
 * 我的试题服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyQuestionServiceImpl extends BaseServiceImp<MyQuestion> implements MyQuestionService {
	@Resource
	private MyQuestionDao myQuestionDao;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private MyExamService myExamService;
	
	@Override
	@Resource(name = "myQuestionDaoImpl")
	public void setDao(BaseDao<MyQuestion> dao) {
		super.dao = dao;
	}

	@Override
	public MyQuestion getMyQuestion(Integer examId, Integer userId, Integer questionId) {
		return myQuestionDao.getMyQuestion(examId, userId, questionId);
	}

	@Override
	public List<MyQuestion> getList(Integer examId, Integer userId) {
		return myQuestionDao.getList(examId, userId);
	}

	@Override
	public List<Map<String, Object>> getAnswerList(Integer examId, Integer userId) {
		return myQuestionDao.getAnswerList(examId, userId);
	}

	@Override
	public void createPaper(Integer examId) {
		MyExam myExam = myExamService.getMyExam(examId, getCurUser().getId());
		if (myExam != null) {
			myExam.setAnswerStartTime(null);
			myExam.setAnswerEndTime(null);
			myExam.setMarkStartTime(null);
			myExam.setMarkEndTime(null);
			myExam.setObjectiveScore(null);
			myExam.setTotalScore(null);
			myExam.setState(1);// 未考试
			myExam.setMarkState(1);// 未阅卷
			myExamService.update(myExam);
			
			List<MyQuestion> myQuestionList = getList(examId, getCurUser().getId());
			for (MyQuestion myQuestion : myQuestionList) {
				myQuestion.setAnswerTime(null);
				myQuestion.setUserAnswer(null);
				myQuestion.setUserScore(null);
				myQuestion.setMarkUserId(null);
				myQuestion.setMarkTime(null);
				update(myQuestion);
			}
		} else {
			myExam = new MyExam();
			myExam.setExamId(examId);
			myExam.setUserId(getCurUser().getId());
			myExam.setMarkUserId(1);
			myExam.setState(1);// 未考试
			myExam.setMarkState(1);// 未阅卷
			myExam.setUpdateTime(new Date());
			myExam.setUpdateUserId(getCurUser().getId());
			myExamService.add(myExam);
			
			List<ExamQuestion> examQuestionList = examQuestionService.getList(examId);
			for (ExamQuestion examQuestion : examQuestionList) {
				MyQuestion myQuestion = new MyQuestion();
				try {
					BeanUtils.copyProperties(myQuestion, examQuestion);// 复制试卷到用户试卷
				} catch (Exception e) {
					throw new MyException("生成试题失败");
				}
				myQuestion.setUserId(getCurUser().getId());
				add(myQuestion);
			}
		}
	}
	
}
