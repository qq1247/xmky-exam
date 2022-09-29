package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.MyExamDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.ex.ExamAnswerEx;
import com.wcpdoc.exam.core.entity.ex.MyExamChapter;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.QuestionUtil;
import com.wcpdoc.file.service.FileService;

/**
 * 我的考试服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyExamServiceImpl extends BaseServiceImp<MyExam> implements MyExamService {
	private static final Logger log = LoggerFactory.getLogger(MyExamServiceImpl.class);
	
	@Resource
	private MyExamDao myExamDao;
	@Resource
	private QuestionService questionService;
	@Resource
	private ExamService examService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private FileService fileService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private UserService userService;

	@Override
	@Resource(name = "myExamDaoImpl")
	public void setDao(BaseDao<MyExam> dao) {
		super.dao = dao;
	}

	@Override
	public List<MyExam> getList(Integer examId) {
		return myExamDao.getList(examId);
	}
	
	@Override
	public List<MyExam> getListForUser(Integer userId) {
		return myExamDao.getListForUser(userId);
	}
	
	@Override
	public MyExam getMyExam(Integer examId, Integer userId) {
		return myExamDao.getMyExam(examId, userId);
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

		MyQuestion myQuestion = myQuestionService.getMyQuestion(examId, userId, questionId);
		if (myQuestion == null) {
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
		MyExam myExam = getMyExam(examId, userId);
		if (myExam.getState() == 3) {
			throw new MyException("考试已交卷");
		}

		// 标记为考试中
		myExam.setState(2);
		update(myExam);
		
		// 保存答案
		Question question = questionService.getEntity(questionId);
		if (!ValidateUtil.isValid(answers)) {
			myQuestion.setAnswer(null);
		} else if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasTrueFalse(question) || QuestionUtil.hasQA(question)) {
			myQuestion.setAnswer(answers[0]);
		} else if (QuestionUtil.hasMultipleChoice(question)) {
			Arrays.sort(answers);// 页面先选d在选c，值为db，这里重新排序一下
			myQuestion.setAnswer(StringUtil.join(answers));
		} else if (QuestionUtil.hasFillBlank(question)) {
			myQuestion.setAnswer(StringUtil.join(answers, '\n'));
		}
		myQuestion.setAnswerTime(new Date());
		myQuestionService.update(myQuestion);
		
		// 保存附件
		if (ValidateUtil.isValid(answerFileId)) {
			fileService.upload(answerFileId);
		}
	}

	@Override
	public void finish(Integer examId, Integer userId) {
//		// 校验数据有效性
//		if (!ValidateUtil.isValid(examId)) {
//			throw new MyException("参数错误：examId");
//		}
//		if (!ValidateUtil.isValid(userId)) {
//			throw new MyException("参数错误：userId");
//		}
//		MyExam myExam = getMyExam(examId, userId);
//		if (myExam == null) {
//			throw new MyException("未参与考试");
//		}
//		if (myExam.getState() == 3) {
//			throw new MyException("考试已交卷");
//		}
//		Exam exam = examService.getEntity(examId);
//		if (exam.getState() == 0) {
//			throw new MyException("考试已删除");
//		}
//		if (exam.getState() == 2) {
//			throw new MyException("考试未发布");
//		}
//		long curTime = System.currentTimeMillis();
//		if (exam.getStartTime().getTime() > curTime) {
//			throw new MyException("考试未开始");
//		}
//		if (curTime - exam.getEndTime().getTime() > 1000) {// 预留1秒网络延时
//			throw new MyException("考试已结束");
//		}
//		
//		// 交卷
//		myExam.setState(3);// 标记为交卷
//		update(myExam);
//	
//		User user = userService.getEntity(myExam.getUserId());
//		log.info("用户交卷开始：【{}-{}】【1-管理员 阅 {}-{}】开始", exam.getId(), exam.getName(), user.getId(), user.getName());
//		MyExamChapter userPaper = exam.getGenType() == 1 ? examService.getExamChapter(exam.getId()) : examService.getPaperOfRand(examId, myExam.getUserId());
//		List<MyQuestion> userAnswerList = myQuestionService.getList(examId, myExam.getUserId());
//		Map<Integer, MyQuestion> userAnswerCache = new HashMap<>();
//		for (MyQuestion userAnswer : userAnswerList) {
//			userAnswerCache.put(userAnswer.getQuestionId(), userAnswer);// 缓存答案数据，用于和标准答案对比得分
//		}
//		BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
//		for (Chapter chapter : userPaper.getChapterList()) {
//			for (MyQuestion myquestion : chapter.getMyQuestionList()) {// 获取章节下所有试题
//				if (!QuestionUtil.hasObjective(myquestion.getQuestion())) {// 如果是主观题，等待人工阅卷
//					continue;
//				}
//				
//				MyQuestion userAnswer = userAnswerCache.get(myquestion.getQuestion().getId());// 获取用户答案
//				if (QuestionUtil.hasQA(myquestion.getQuestion())) { 
//					qAHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 问答处理
//				} else if (QuestionUtil.hasSingleChoice(myquestion.getQuestion()) || QuestionUtil.hasTrueFalse(myquestion.getQuestion())) {
//					singleChoiceHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 单选判断处理
//				} else if (QuestionUtil.hasMultipleChoice(myquestion.getQuestion())) { 
//					multipleChoiceHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 多选处理
//				} else if (QuestionUtil.hasFillBlank(myquestion.getQuestion())) { 
//					fillBlankHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 填空处理
//				}
//				
//				totalScore.add(userAnswer.getScore());// 累加当前分数到总分数
//				userAnswer.setMarkTime(new Date());
//				userAnswer.setMarkUserId(1);
//				myQuestionService.update(userAnswer);// 更新每道题的分数（没作答也都标记为0分。影响的地方为人工阅卷时，所有题都有分数，才允许阅卷完成。）
//			}
//		}
//		
//		if (exam.getMarkType() == 1) {// 如果是客观题试卷，直接出成绩
//			myExam.setMarkUserId(1);// 阅卷人为admin
//			myExam.setMarkState(3);// 标记为阅卷结束
//			myExam.setTotalScore(totalScore.getResult());// 记录成绩 
//			BigDecimal passScore = BigDecimalUtil.newInstance(exam.getTotalScore())
//					.mul(exam.getPassScore()).div(100, 2).getResult();
//			myExam.setAnswerState(BigDecimalUtil.newInstance(totalScore.getResult()).sub(passScore).getResult().doubleValue() >= 0 ? 1 : 2);// 标记及格状态
//			
//			update(myExam);
//		} 
//		
//		if (exam.getMarkType() == 1) {
//			log.info("用户交卷完成：【{}-{}】【1-管理员 阅 {}-{}】完成阅卷，得{}分，{}", exam.getId(), exam.getName(), user.getId(), user.getName(), 
//					totalScore.getResult(), myExam.getAnswerState() == 1 ? "及格" : "不及格");
//		} else if (exam.getMarkType() == 2) {// 如果是主观题试卷，等待人工阅卷
//			log.info("用户交卷完成：【{}-{}】【1-管理员 阅 {}-{}】完成阅卷，客观题部分得{}分", exam.getId(), exam.getName(), user.getId(), user.getName(), 
//					totalScore.getResult());
//		}
	}
	
	@Override
	public void doExam(Integer examId) {
		// 延时2秒后开始（答题时预留了1秒网络延时，这里在延时1秒，保证都是答题完成后的结果）
		Exam exam = examService.getEntity(examId);
		doExamTimedelay(exam);// 业务上变更考试时间为当前时间时，自动阅卷概率性校验为考试未结束（数据库时间四舍五入后比当前时间大。例：传入值：2022-05-12 23:59:59,999999，保存为 2022-05-13 00:00:00），所以先延时后校验
		
		// 校验数据有效性
		doExamValid(exam);
		
		// 获取考试用户列表
		log.info("客观题阅卷开始：【{}-{}】", exam.getId(), exam.getName());
		List<MyExam> myExamList = myExamService.getList(examId);
		
		// 获取用户试卷、标准答案
		MyExamChapter userPaper; // exam.getGenType() == 1 ? paperService.getPaper(paper.getId()) : null;// 人工组卷每个人是一样的
		for (MyExam myExam : myExamList) {
			userPaper = null; // exam.getGenType() == 2 ? paperService.getPaperOfRand(examId, myExam.getUserId()) : userPaper;// 随机组卷每个人是不一样的
			
			// 开始阅卷
			doExamHandle(exam, myExam, userPaper);
		}
		
		// 更新用户排名
		doExamRank(exam, myExamList);
		
		// 完成考试
		doExamFinish(exam);
	}

	private void doExamFinish(Exam exam) {
		if (exam.getMarkType() == 1) {
			exam.setMarkState(3);// 标记考试为已阅卷
			examService.update(exam);
			log.info("客观题阅卷完成：标记考试为已阅卷，结束");
			
			AutoMarkCache.del(exam.getId());// 不在runner类删除是因为，如果是主观题试卷，缓存需要重新放入一份
		} else if (exam.getMarkType() == 2) {
			exam.setMarkState(2);// 标记考试为阅卷中，等待人工阅卷。（人工阅卷校时先校验状态是否变更为2，保证顺序执行）
			examService.update(exam);
			log.info("客观题阅卷完成：标记考试为阅卷中，等待人工阅卷");
			
			AutoMarkCache.put(exam.getId(), exam);// 重新放入缓存是因为，考试的阅卷状态已经变更
		}
	}

	private void doExamTimedelay(Exam exam) {
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			log.error("客观题阅卷异常：【{}-{}】延时执行异常", exam.getId(), exam.getName());
			throw new MyException("延时执行异常");
		}
	}

	private void doExamValid(Exam exam) {
		// 校验数据有效性
		log.info("客观题阅卷校验：【{}-{}】", exam.getId(), exam.getName());
		if (exam.getState() == 0) {
			log.error("客观题阅卷异常：【{}-{}】已删除", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】已删除", exam.getId(), exam.getName()));
		}
		if (exam.getState() == 2) {
			log.error("客观题阅卷异常：【{}-{}】未发布", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】未发布", exam.getId(), exam.getName()));
		}
		if (exam.getState() == 3) {
			log.error("客观题阅卷异常：【{}-{}】已归档", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】已归档", exam.getId(), exam.getName()));
		}
		Date curTime = new Date();
		if (exam.getEndTime().getTime() > curTime.getTime()){
			log.error("客观题阅卷异常：【{}-{}】考试未结束", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】考试未结束", exam.getId(), exam.getName()));
		}
		if (exam.getMarkState() != 1) {// 等于2也表示自动阅卷已完成
			log.error("客观题阅卷异常：【{}-{}】已阅卷", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】已阅卷", exam.getId(), exam.getName()));
		}
	}
	
	private void doExamHandle(Exam exam, MyExam myExam, MyExamChapter userPaper) {
//		User user = userService.getEntity(myExam.getUserId());
//		if(myExam.getState() == 3){// 已交卷不处理（已阅卷）
//			log.info("客观题阅卷进行：【{}-{}】，【{}-{}】已阅卷，得{}分", exam.getId(), exam.getName(), user.getId(), user.getName(), myExam.getTotalScore());
//			return;
//		}
//		
//		log.info("客观题阅卷进行：【{}-{}】【1-管理员 阅 {}-{}】开始", exam.getId(), exam.getName(), user.getId(), user.getName());
//		if (myExam.getState() == 1) {// 如果未考试，状态还是未考试
//			log.info("客观题阅卷进行：【{}-{}】【1-管理员 阅 {}-{}】未考试", exam.getId(), exam.getName(), user.getId(), user.getName());
//		} else if (myExam.getState() == 2) {// 如果是考试中，标记为交卷
//			myExam.setState(3);
//			log.info("客观题阅卷进行：【{}-{}】【1-管理员 阅 {}-{}】未交卷，标记为已交卷", exam.getId(), exam.getName(), user.getId(), user.getName());
//		}
//		
//		BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
//		Map<Integer, MyQuestion> userAnswerCache = new HashMap<>();
//		List<MyQuestion> userAnswerList = myQuestionService.getList(exam.getId(), myExam.getUserId());
//		for (MyQuestion userAnswer : userAnswerList) {
//			userAnswerCache.put(userAnswer.getQuestionId(), userAnswer);// 缓存答案数据，用于和标准答案对比得分
//		}
//		for (Chapter chapter : userPaper.getChapterList()) {
//			for (MyQuestion myquestion : chapter.getMyQuestionList()) {// 获取章节下所有试题
//				MyQuestion userAnswer = userAnswerCache.get(myquestion.getQuestion().getId());// 获取用户答案
//				if (myExam.getState() == 1) {// 如果未考试，标记用户当前题为0分（阅卷时需要所有题都有分数才能进行）
//					userAnswer.setMarkTime(new Date());
//					userAnswer.setMarkUserId(1);
//					userAnswer.setScore(BigDecimal.ZERO);
//					myQuestionService.update(userAnswer);
//					continue;
//				}
//				
//				if (!QuestionUtil.hasObjective(myquestion.getQuestion())) {// 如果是主观题，等待人工阅卷
//					continue;
//				}
//				
//				if (QuestionUtil.hasQA(myquestion.getQuestion())) { 
//					qAHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 问答处理
//				} else if (QuestionUtil.hasSingleChoice(myquestion.getQuestion()) || QuestionUtil.hasTrueFalse(myquestion.getQuestion())) {
//					singleChoiceHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 单选判断处理
//				} else if (QuestionUtil.hasMultipleChoice(myquestion.getQuestion())) { 
//					multipleChoiceHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 多选处理
//				} else if (QuestionUtil.hasFillBlank(myquestion.getQuestion())) { 
//					fillBlankHandle(myquestion.getQuestion(), myquestion.getAttr(), myquestion.getAnswerList(), userAnswer);// 填空处理
//				}
//				
//				totalScore.add(userAnswer.getScore());// 累加当前分数到总分数
//				userAnswer.setMarkTime(new Date());
//				userAnswer.setMarkUserId(1);
//				myQuestionService.update(userAnswer);// 更新每道题的分数（没作答也都标记为0分。影响的地方为人工阅卷时，所有题都有分数，才允许阅卷完成。）
//			}
//		}
//		
//		if (exam.getMarkType() == 1 || myExam.getState() == 1) {// 如果是智能阅卷或未考试，直接出成绩
//			myExam.setMarkUserId(1);// 阅卷人为admin
//			myExam.setMarkState(3);// 标记为阅卷结束
//			myExam.setTotalScore(totalScore.getResult());// 记录成绩 
//			BigDecimal passScore = BigDecimalUtil.newInstance(exam.getTotalScore())
//					.mul(exam.getPassScore()).div(100, 2).getResult();
//			myExam.setAnswerState(BigDecimalUtil.newInstance(totalScore.getResult()).sub(passScore).getResult().doubleValue() >= 0 ? 1 : 2);// 标记及格状态
//			myExamService.update(myExam);
//		}
//		
//		if (exam.getMarkType() == 1) {
//			log.info("客观题阅卷进行：【{}-{}】【1-管理员 阅 {}-{}】完成阅卷，得{}分，{}", exam.getId(), exam.getName(), user.getId(), user.getName(), 
//					totalScore.getResult(), myExam.getAnswerState() == 1 ? "及格" : "不及格");
//		} else {
//			log.info("客观题阅卷进行：【{}-{}】【1-管理员 阅 {}-{}】完成阅卷，客观题部分得{}分", exam.getId(), exam.getName(), user.getId(), user.getName(), 
//					totalScore.getResult());
//		}
	}

	@Override
	public void doMark(Integer examId) {
		// 延时2秒在阅卷（阅卷时预留了1秒网络延时，这里在延时1秒，保证都是人工阅卷完成后的结果）
		Exam exam = examService.getEntity(examId);
		doMarkTimedelay(exam);// 参考doExam(Integer examId)接口
		
		// 校验数据有效性
		doMarkValid(exam);
		
		// 获取考试用户列表
		log.info("主观题阅卷开始：【{}-{}】", exam.getId(), exam.getName());
		List<MyExam> myExamList = myExamService.getList(examId);// 考试用户列表
		for (MyExam myExam : myExamList) {
			List<MyQuestion> userAnswerList = myQuestionService.getList(myExam.getExamId(), myExam.getUserId());// 用户答案
			doMarkHandle(exam, myExam, userAnswerList);
		}
		
		// 更新用户排名
		doMarkRank(myExamList);
		
		// 完成考试
		doMarkFinish(exam);
	}

	private void doMarkTimedelay(Exam exam) {
		try {
			TimeUnit.SECONDS.sleep(2);// 请参考doExam()说明
		} catch (InterruptedException e) {
			log.error("主观题阅卷异常：【{}-{}】延时执行异常", exam.getId(), exam.getName());
		}
	}

	private void doMarkValid(Exam exam) {
		log.info("主观题阅卷校验：【{}-{}】", exam.getId(), exam.getName());
		
		if (exam.getState() == 0) {
			log.error("主观题阅卷异常：【{}-{}】已删除", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】已删除", exam.getId(), exam.getName()));
		}
		if (exam.getState() == 2) {
			log.error("主观题阅卷异常：【{}-{}】未发布", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】未发布", exam.getId(), exam.getName()));
		}
		if (exam.getState() == 3) {
			log.error("主观题阅卷异常：【{}-{}】已归档", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】已归档", exam.getId(), exam.getName()));
		}
		if (exam.getMarkState() == 1) {// 客观题阅卷没阅完
			log.error("主观题阅卷异常：【{}-{}】智能题未阅卷", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】智能题未阅卷", exam.getId(), exam.getName()));
		}
		if (exam.getMarkState() == 3) {
			log.error("主观题阅卷异常：【{}-{}】已阅卷", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】已阅卷", exam.getId(), exam.getName()));
		}
		
		long curTime = System.currentTimeMillis();
		if (exam.getEndTime().getTime() > curTime) {
			log.error("主观题阅卷异常：【{}-{}】考试未结束", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】考试未结束", exam.getId(), exam.getName()));
		}
		if (exam.getMarkEndTime().getTime() > curTime) {
			log.error("主观题阅卷异常：【{}-{}】阅卷未结束", exam.getId(), exam.getName());
			throw new MyException(String.format("【%s-%s】阅卷未结束", exam.getId(), exam.getName()));
		}
	}
	
	private void doMarkHandle(Exam exam, MyExam myExam, List<MyQuestion> userAnswerList) {
		User examUser = userService.getEntity(myExam.getUserId());
		if (myExam.getMarkState() == 3) {//已阅卷的不处理（没考试的人考试时间结束已阅卷；人工阅卷在阅卷时间结束之前已经部分试卷阅完）
			User markUser = userService.getEntity(myExam.getMarkUserId());
			log.info("主观题阅卷进行：【{}-{}】【{}-{} 已阅 {}-{}】{}，不处理", exam.getId(), exam.getName(), 
					markUser.getId(), markUser.getName(), examUser.getId(), examUser.getName(), myExam.getState() == 1 ? "未考试" : "已阅卷");
			return;
		}
		
		// 开始补全未处理的阅卷数据，并合计成绩
		if (myExam.getMarkState() == 1) {// 试卷无人阅卷
			myExam.setMarkUserId(1);//记录阅卷人为admin
			log.info("主观题阅卷进行：【{}-{}】【1-管理员 阅 {}-{}】，无人阅卷", exam.getId(), exam.getName(), examUser.getId(), examUser.getName());
		} else if (myExam.getMarkState() == 2) {// 试卷部分阅完
			User markUser = userService.getEntity(myExam.getMarkUserId());
			log.info("主观题阅卷进行：【{}-{}】【{}-{} 阅 {}-{}】，部分阅完", exam.getId(), exam.getName(), 
					markUser.getId(), markUser.getName(), examUser.getId(), examUser.getName());
		}
		
		
		BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
		for (MyQuestion userAnswer : userAnswerList) {
			if (userAnswer.getScore() == null) {//当阅卷人没有阅卷或部分未阅卷时，阅卷时间到。
				userAnswer.setScore(BigDecimal.ZERO);// 标记该题为0分
				userAnswer.setMarkUserId(1);// 阅卷人admin
				userAnswer.setMarkTime(new Date());// 阅卷时间为当前时间
				myQuestionService.update(userAnswer);
				continue;
			}
			
			totalScore.add(userAnswer.getScore());// 累加所有题分数到总分数
		}
		
		myExam.setTotalScore(totalScore.getResult());// 记录成绩 
		BigDecimal passScore = BigDecimalUtil.newInstance(exam.getTotalScore()).mul(exam.getPassScore()).div(100, 2).getResult();
		myExam.setAnswerState(BigDecimalUtil.newInstance(totalScore.getResult()).sub(passScore).getResult().doubleValue() >= 0 ? 1 : 2);// 标记及格状态
		myExam.setMarkState(3);// 标记为阅卷结束
		myExamService.update(myExam);
		log.info("主观题阅卷进行：【{}-{}】【1-管理员 阅 {}-{}】，得{}分，{}", exam.getId(), exam.getName(), examUser.getId(), examUser.getName(), 
				totalScore.getResult(), myExam.getAnswerState() == 1 ? "及格" : "不及格");
	}

	private void doMarkRank(List<MyExam> myExamList) {
		Collections.sort(myExamList, new Comparator<MyExam>() {
			@Override
			public int compare(MyExam o1, MyExam o2) {
				return o2.getTotalScore().compareTo(o1.getTotalScore());
			}
		});
		
		for (int i = 0; i < myExamList.size(); i++) {
			myExamList.get(i).setNo(i + 1);
			myExamService.update(myExamList.get(i));
		}
	}
	
	private void doMarkFinish(Exam exam) {
		exam.setMarkState(3);
		examService.update(exam);
		log.info("主观题阅卷结束：标记考试为已阅卷，结束");
		
		AutoMarkCache.del(exam.getId());// 清除阅卷监听
	}

	/**
	 * 用户排名
	 * 
	 * v1.0 zhanghc 2022年1月26日下午1:55:49
	 * @param myExamList void
	 */
	private void doExamRank(Exam exam, List<MyExam> myExamList) {
		if (exam.getMarkType() != 1) {// 主观题试卷需要人工阅卷后才能排序
			return;
		}
		
		Collections.sort(myExamList, new Comparator<MyExam>() {
			@Override
			public int compare(MyExam o1, MyExam o2) {
				return o2.getTotalScore().compareTo(o1.getTotalScore());
			}
		});
		
		for (int i = 0; i < myExamList.size(); i++) {
			myExamList.get(i).setNo(i + 1);
			myExamService.update(myExamList.get(i));
		}
	}
	
	/**
	 * 问答处理
	 * 
	 * v1.0 chenyun 2021年7月21日下午2:09:40
	 * @param question 试题
	 * @param examQuestion 试题选项
	 * @param examAnswerList 考试试题
	 * @param userAnswer 用户答案
	 * void
	 */
	private void qAHandle(Question question, ExamQuestion examQuestion, 
			List<ExamAnswerEx> examAnswerList, MyQuestion userAnswer) {
		if (QuestionUtil.hasObjective(question)) {// 试题类型为人工阅卷，不处理
			return;
		}
		
		userAnswer.setScore(BigDecimal.ZERO);// 先初始化，防止多次调用分数累加
		if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果用户没有作答，不处理
			return;
		}
		
		boolean dxxbmg = QuestionUtil.dxxbmg(examQuestion);// 大小写不敏感
		String _userAnswer = dxxbmg
				? userAnswer.getAnswer().toLowerCase()
				: userAnswer.getAnswer();// 获取用户答案
		for (ExamAnswerEx examAnswer : examAnswerList) {// 获取试题某一项关键词
			String[] _questionAnswers = dxxbmg 
					? examAnswer.getAnswer().toLowerCase().split("\n") 
					: examAnswer.getAnswer().split("\n");// 获取关键词的所有同义词
			for (String _questionAnswer : _questionAnswers) {// 用户答案和同义词对比
				if (_userAnswer.contains(_questionAnswer)) {// 如果找到（对比条件不要反，用户答案是大段文字）
					userAnswer.setScore(BigDecimalUtil.newInstance(userAnswer.getScore())
							.add(examAnswer.getScore()).getResult());// 累计该关键词的分数
					break;// 匹配到一个同义词就结束；返回大循环继续对比其他关键词
				}
			}
		}
	}
	
	/**
	 * 单选处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:06:10
	 * @param question
	 * @param examQuestion
	 * @param examAnswerList 
	 * @param userAnswer 
	 * void
	 */
	private void singleChoiceHandle(Question question, ExamQuestion examQuestion, 
			List<ExamAnswerEx> examAnswerList, MyQuestion userAnswer) {
		if (QuestionUtil.hasObjective(question)) {// 试题类型为人工阅卷，不处理
			return;
		}
		
		userAnswer.setScore(BigDecimal.ZERO);// 先初始化，防止多次调用分数累加
		if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果用户没有作答，不处理
			return;
		}
		
		for (ExamAnswerEx examAnswer : examAnswerList) {// 就一个答案，可以不循环
			if (examAnswer.getAnswer().equals(userAnswer.getAnswer())) {
				userAnswer.setScore(examAnswer.getScore());
			}
		}
	}
	
	/**
	 * 多选处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:11:47
	 * @param questionAnswer
	 * @param question
	 * @param userAnswer 
	 * void
	 */
	private void multipleChoiceHandle(Question question, ExamQuestion examQuestion, 
			List<ExamAnswerEx> examAnswerList, MyQuestion userAnswer) {
		if (QuestionUtil.hasObjective(question)) {// 试题类型为人工阅卷，不处理
			return;
		}
		
		userAnswer.setScore(BigDecimal.ZERO);// 先初始化，防止多次调用分数累加
		if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果用户没有作答，不处理
			return;
		}
		
		Set<String> userAnswerSet = new HashSet<String>(Arrays.asList(userAnswer.getAnswer().split(",")));// 获取用户答案
		Set<String> questionAnswerSet = new HashSet<String>();// 获取试题答案
		for(ExamAnswerEx examAnswer : examAnswerList){
			String[] questionAnswerSplit = examAnswer.getAnswer().split(",");
			for (int i = 0; i < questionAnswerSplit.length; i++) {
				questionAnswerSet.add(questionAnswerSplit[i]);
			}
		}
		
		if (questionAnswerSet.size() == userAnswerSet.size() && questionAnswerSet.containsAll(userAnswerSet)) {// 如果完全正确，得满分
			userAnswer.setScore(examQuestion.getScore());
		} else if (QuestionUtil.lxdf(examQuestion) && questionAnswerSet.containsAll(userAnswerSet)) {// 如果勾选了漏选得分，得漏选的分
			userAnswer.setScore(examAnswerList.get(0).getScore());
		} else {// 如果不对得0分
			userAnswer.setScore(BigDecimal.ZERO);
		}
	}

	/**
	 * 填空处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:11:55
	 * @param questionAnswer
	 * @param question
	 * @param userAnswer 
	 * void
	 */
	
	public static void main(String[] args) {
//		String[] answers = new String[] {null, "", null, ""};
//		String join = StringUtil.join(answers, '\n');
//		System.err.println(join);
		
		Question question = new Question();
		question.setMarkType(1);
		
		ExamQuestion examQuestion = new ExamQuestion();
		examQuestion.setMarkOptions("2,3");
		
		List<ExamAnswerEx> examAnswerList = new ArrayList<>();
		ExamAnswerEx examAnswer = new ExamAnswerEx();
		examAnswer.setAnswer("奖学金");
		examAnswer.setScore(new BigDecimal("1"));
		examAnswerList.add(examAnswer);
		
		examAnswer = new ExamAnswerEx();
		examAnswer.setAnswer("助学金");
		examAnswer.setScore(new BigDecimal("1"));
		examAnswerList.add(examAnswer);
		
		examAnswer = new ExamAnswerEx();
		examAnswer.setAnswer("贷学金");
		examAnswer.setScore(new BigDecimal("1"));
		examAnswerList.add(examAnswer);
		
		MyQuestion userAnswer = new MyQuestion();
		userAnswer.setAnswer("助学金\n奖学金\n奖学金");
		
		new MyExamServiceImpl().fillBlankHandle(question, examQuestion, examAnswerList, userAnswer);
		System.err.println(userAnswer.getScore());
	}
	private void fillBlankHandle(Question question, ExamQuestion examQuestion, 
			List<ExamAnswerEx> examAnswerList, MyQuestion userAnswer) {
		if (QuestionUtil.hasObjective(question)) {// 试题类型为人工阅卷，不处理
			return;
		}
		
		userAnswer.setScore(BigDecimal.ZERO);// 先初始化，防止多次调用分数累加
		if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果用户没有作答，不处理
			return;
		}
		
		/**
		 * 涉密人员上岗前要经过_______和_______。
		 * 关键词一：保密审查  保密调查
		 * 关键词二：培训   岗前培训
		 * 用户答案：培训  审查
		 * 匹配结果：【培训】得分；【审查】不得分
		 */
		boolean dxxbmg = QuestionUtil.dxxbmg(examQuestion);// 大小写不敏感
		String[] userAnswers = dxxbmg
				? userAnswer.getAnswer().toLowerCase().split("\n")
				: userAnswer.getAnswer().split("\n");// 获取用户答案（多空就是多个答案）
		Set<Integer> useAnswers = new HashSet<>();// bug：考试答题-》填空题（3个空）-》属性为客观题、填空允许乱序-》一个正确答案分别填到三个空上-》当前题满分
		for (int i = 0; i < userAnswers.length; i++) {// 循环用户每一项答案（[培训  审查]）
			for (int j = 0; j < examAnswerList.size(); j++) {// 循环每一项试题关键词（[[保密审查,保密调查], [培训  审查]]）
				ExamAnswerEx examAnswer = examAnswerList.get(j);
				String[] synonyms = dxxbmg 
						? examAnswer.getAnswer().toLowerCase().split("\n") 
						: examAnswer.getAnswer().split("\n");// 获取关键词的所有同义词
						
				if (!QuestionUtil.dawsx(examQuestion)) {// 如果勾选了答案前后有顺序，则对应位置对比
					if (i != j) {// 不是对应位置，返回继续查找
						continue;
					}
				}
				if (useAnswers.contains(j)) {
					continue;
				}
				
				for (String synonym : synonyms) {// 循环每一项同义词（保密审查  保密调查）
					if (userAnswers[i].contains(synonym)) {// 如果用户某一空答案，匹配某一项关键词的同义词
						userAnswer.setScore(BigDecimalUtil.newInstance(userAnswer.getScore())
								.add(examAnswer.getScore()).getResult());// 累计该关键词的分数
						useAnswers.add(j);
						break;// 匹配到一个同义词就结束；返回大循环继续对比其他关键词
					}
				}
				if (useAnswers.contains(j)) {
					break;// 处理下一个用户答案（作用于上一个for循环，用户答案匹配某个标准答案，就不在循环其他标准答案）
				}
			}
		}
	}

	@Override
	public List<Exam> getExamList(Integer userId) {
		return myExamDao.getExamList(userId);
	}

}
