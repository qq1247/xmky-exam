package com.wcpdoc.exam.core.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.wcpdoc.base.entity.User;
import com.wcpdoc.base.service.BaseCacheService;
import com.wcpdoc.core.dao.RBaseDao;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.impl.BaseServiceImp;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.constant.ExamConstant;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.ex.PaperPart;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyPaperService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.util.ExamUtil;
import com.wcpdoc.exam.core.util.MyExamUtil;
import com.wcpdoc.exam.core.util.QuestionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
@Slf4j
public class MyExamServiceImpl extends BaseServiceImp<MyExam> implements MyExamService {

	@Resource
	private MyExamDao myExamDao;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private MyPaperService myPaperService;
	@Resource
	private ExamCacheService examCacheService;
	@Resource
	private BaseCacheService baseCacheService;

	@Override
	public RBaseDao<MyExam> getDao() {
		return myExamDao;
	}

	@Override
	public List<PaperPart> paper(Integer examId, Integer userId) {
		// 数据校验
		paperValid(examId, userId);

		// 试卷处理
		Exam exam = examCacheService.getExam(examId);
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (!ValidateUtil.isValid(myExam.getExamStartTime())
				&& exam.getEndTime().getTime() > System.currentTimeMillis()) {
			SpringUtil.getBean(MyExamService.class).paperHandle(examId, userId);// 接口调用缓存注解才生效
		}
		// 试卷生成
		return myPaperService.generatePaper(examId, userId, MyExamUtil.scoreShow(exam, myExam),
				MyExamUtil.answerShow(exam, myExam));
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY), })
	public void paperHandle(Integer examId, Integer userId) {
		/**
		 * 考试用户如果是第一次打开试卷，并且考试没结束，生成他自己的考试时间，并标记为考试中<br/>
		 * 
		 * 如果是限制考试，从当前开始计时，延长到限制的分钟数则结束，但不超过本次考试的结束时间<br/>
		 * 示例：考试时间为2024-01-30 08:00 - 2024-01-30 12:00，单次考试时间为60分钟<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 08:15:16；他的考试结束时间为2024-01-30 09:15:16<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 11:15:16；他的考试结束时间为2024-01-30 12:00:00<br/>
		 * 
		 * 如果是不限制考试，从当前开始计时，他的结束时间为本次考试的结束时间<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 08:15:16；他的考试结束时间为2024-01-30 12:00:00<br/>
		 * 考试用户第一次打开试卷时间为2024-01-30 11:15:16；他的考试结束时间为2024-01-30 12:00:00<br/>
		 */
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		Exam exam = examCacheService.getExam(examId);
		myExam.setExamStartTime(new Date());
		if (ExamUtil.hasTimeLimit(exam)) {
			myExam.setExamEndTime(DateUtil.getNextMinute(myExam.getExamStartTime(), exam.getLimitMinute()));
			if (myExam.getExamEndTime().getTime() > exam.getEndTime().getTime()) {
				myExam.setExamEndTime(exam.getEndTime());
			}
		} else {
			myExam.setExamEndTime(exam.getEndTime());
		}
		myExam.setState(2);
		updateById(myExam);

		User user = baseCacheService.getUser(myExam.getUserId());
		log.info("【{}-{}】进入【{}-{}】考试", user.getLoginName(), user.getName(), exam.getId(), exam.getName());
	}

	@Override
	@CacheEvict(value = ExamConstant.MYQUESTION_CACHE, key = ExamConstant.MYQUESTION_LIST_KEY_PRE
			+ "#examId + ':' + #userId")
	public void answer(Integer examId, Integer userId, Integer questionId, String[] answers) {
		// 数据校验
		MyQuestion myQuestion = answerValid(examId, userId, questionId);

		// 答案保存
		answerSave(questionId, answers, myQuestion);

		User user = baseCacheService.getUser(userId);
		Exam exam = examCacheService.getExam(examId);
		log.info("【{}-{}】正在【{}-{}】答题，【{}-{}】", user.getLoginName(), user.getName(), exam.getId(), exam.getName(),
				questionId, StringUtil.join(answers));
	}

	@Override
	@Caching(evict = { //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_KEY_PRE
					+ "#examId + ':' + #userId"),
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_LIST_KEY_PRE + "#examId"), //
			@CacheEvict(value = ExamConstant.MYEXAM_CACHE, key = ExamConstant.MYEXAM_UNMARK_LIST_KEY), //
	})
	public void finish(Integer examId, Integer userId) {
		// 数据校验
		finishValid(examId, userId);

		// 交卷
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		myExam.setExamEndTime(new Date());
		updateById(myExam);

		User user = baseCacheService.getUser(myExam.getUserId());
		Exam exam = examCacheService.getExam(examId);
		log.info("【{}-{}】考试，【{}-{}】交卷", exam.getId(), exam.getName(), user.getLoginName(), user.getName());
	}

	@Override
	@CacheEvict(value = ExamConstant.MYEXAM_CACHE, allEntries = true)
	public void clear(Integer examId) {
		myExamDao.clear(examId);
	}

	private void paperValid(Integer examId, Integer userId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}

		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("试卷不存在");
		}
		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		if (exam.getStartTime().getTime() > System.currentTimeMillis()) {
			throw new MyException("考试未开始");
		}
	}

	private void answerSave(Integer questionId, String[] answers, MyQuestion myQuestion) {
		Question question = examCacheService.getQuestion(questionId);
		if (!ValidateUtil.isValid(answers)) {
			myQuestion.setUserAnswer(null);
		} else if (QuestionUtil.hasTrueFalse(question)) {
			myQuestion.setUserAnswer(answers[0]);
		} else if (QuestionUtil.hasSingleChoice(question)) {
			if (ValidateUtil.isValid(myQuestion.getOptionsNo())) {
				myQuestion.setUserAnswer(myQuestion.getOptionsNoCacheOfAnswer().get(answers[0]));
			} else {
				myQuestion.setUserAnswer(answers[0]);
			}
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			if (ValidateUtil.isValid(myQuestion.getOptionsNo())) {
				for (int i = 0; i < answers.length; i++) {
					answers[i] = myQuestion.getOptionsNoCacheOfAnswer().get(answers[i]);
				}
			}

			Arrays.sort(answers);// 页面先选d在选c，值为db，这里重新排序一下
			myQuestion.setUserAnswer(StringUtil.join(answers));
		} else if (QuestionUtil.hasFillBlank(question)) {
			myQuestion.setUserAnswer(StringUtil.join(answers, '\n'));
		} else if (QuestionUtil.hasQA(question)) {
			myQuestion.setUserAnswer(StringUtil.join(answers));// bug：文本包含英文逗号会分割
		}
		myQuestion.setAnswerTime(new Date());
		myQuestionService.updateById(myQuestion);
	}

	private MyQuestion answerValid(Integer examId, Integer userId, Integer questionId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		if (!ValidateUtil.isValid(questionId)) {
			throw new MyException("参数错误：questionId");
		}
//		if (!ValidateUtil.isValid(answers)) {// 允许清空答案，比如多选
//			throw new MyException("参数错误：answers");
//		} 

		MyQuestion myQuestion = myQuestionService.getMyQuestion(examId, userId, questionId);
		if (myQuestion == null) {
			throw new MyException("未参与考试");
		}

		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		long curTime = System.currentTimeMillis();
		if (myExam.getExamStartTime().getTime() > curTime) {
			throw new MyException("考试未开始");
		}
		if (myExam.getExamEndTime().getTime() < curTime) {
			throw new MyException("考试已结束");
		}
		if (myExam.getState() == 3) {
			throw new MyException("已交卷");
		}
		return myQuestion;
	}

	private void finishValid(Integer examId, Integer userId) {
		if (!ValidateUtil.isValid(examId)) {
			throw new MyException("参数错误：examId");
		}
		if (!ValidateUtil.isValid(userId)) {
			throw new MyException("参数错误：userId");
		}
		MyExam myExam = examCacheService.getMyExam(examId, userId);
		if (myExam == null) {
			throw new MyException("未参与考试");
		}
		if (myExam.getState() == 1) {// 考试时间内，只要进入考试，状态就是2
			throw new MyException("未考试");
		}
		if (myExam.getState() == 3) {
			throw new MyException("已交卷");
		}
		Exam exam = examCacheService.getExam(examId);
		if (exam.getState() == 0) {
			throw new MyException("考试已删除");
		}
		if (exam.getState() == 2) {
			throw new MyException("考试已暂停");
		}
		long curTime = System.currentTimeMillis();
		if (myExam.getExamStartTime().getTime() > curTime) {
			throw new MyException("考试未开始");
		}
//		if (myExam.getExamEndTime().getTime() < curTime + 1) {// 预留1秒网络延时
//			throw new MyException("考试已结束");// 服务部署在低配的电脑上，如果有高并发，可以会造成延时1秒也不够
//		}// 所以只要状态是考试中，就可以更新交卷时间
	}
}
