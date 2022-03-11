package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.dao.MyExamDetailDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionAnswer;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperQuestionAnswerService;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionService;

/**
 * 我的考试详细服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class MyExamDetailServiceImpl extends BaseServiceImp<MyExamDetail> implements MyExamDetailService {
	private static final Logger log = LoggerFactory.getLogger(MyExamDetailServiceImpl.class);
	@Resource
	private MyExamDetailDao myExamDetailDao;
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private QuestionService questionService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private UserService userService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private PaperQuestionAnswerService paperQuestionAnswerService;
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private MyExamDetailService myExamDetailService;
	
	@Override
	@Resource(name = "myExamDetailDaoImpl")
	public void setDao(BaseDao<MyExamDetail> dao) {
		super.dao = dao;
	}

	@Override
	public List<MyExamDetail> getUserAnswerList(Integer examId, Integer userId) {
		return myExamDetailDao.getList(examId, userId);
	}

	@Override
	public List<Map<String, Object>> getAnswerList(Integer examId, Integer userId) {
		return myExamDetailDao.getAnswerList(examId, userId);
	}

	@Override
	public MyExamDetail getEntity(Integer examId, Integer userId, Integer questionId) {
		return myExamDetailDao.getEntity(examId, userId, questionId);
	}
	
	@Override
	public void doExam(Integer examId) {
		// 校验数据有效性
		Exam exam = examService.getEntity(examId);
		log.info("自动考试校验：{}", exam.getName());
		
		if (exam.getState() == 0) {
			log.error("自动考试异常：{}已删除", exam.getName());
			throw new MyException("已删除");
		}
		if (exam.getState() == 2) {
			log.error("自动考试异常：{}未发布", exam.getName());
			throw new MyException("未发布");
		}
		if (exam.getState() == 3) {
			log.error("自动考试异常：{}已归档", exam.getName());
			throw new MyException("已归档");
		}
		if (exam.getMarkState() == 2 || exam.getMarkState() == 3) {
			log.error("自动考试异常：{}已阅卷", exam.getName());
			throw new MyException("已阅卷");
		}
		
		long curTime = System.currentTimeMillis();
		if (exam.getEndTime().getTime() > curTime){
			log.error("自动考试异常：{}考试未结束", exam.getName());
			throw new MyException("考试未结束");
		}
		
		// 延时2秒后开始（答题时预留了1秒网络延时，这里在延时1秒，保证都是答题完成后的结果）
		try {
			TimeUnit.SECONDS.sleep(2);
		} catch (InterruptedException e) {
			log.error("自动考试异常：{}，延时执行异常", exam.getName());
		}
		
		Paper paper = paperService.getEntity(exam.getPaperId());// 试卷信息
		List<MyExam> myExamList = myExamService.getList(examId);// 考试用户列表
		Map<Integer, Question> questionCache = paper.getGenType() == 1 
				? getQuestionCache(exam.getPaperId()) 
				: getQuestionRandCache(exam.getPaperId(), exam.getId());// 试题缓存信息
		Map<Integer, List<PaperQuestionAnswer>> questionAnswerListCache = paper.getGenType() == 1 
				? questionAnswerListCache(exam.getPaperId(), questionCache.values()) 
				: questionRandAnswerListCache(exam.getId(), exam.getPaperId(), questionCache.values());//试题答案缓存信息
		Map<Integer, PaperQuestion> questionOptionCache = paper.getGenType() == 1 
				? questionOptionCache(exam.getPaperId()) 
				: questionRandOptionCache(exam.getId(), exam.getPaperId());;//试题选项缓存信息
		if (paper.getMarkType() == 1) {
			for (Question question : questionCache.values()) {
				if (question.getAi() == 2) {
					log.error("自动考试异常：{}检测到人工阅卷试题", exam.getName());
					throw new MyException("检测到人工阅卷试题");
				}
			}
		}
		
		// 获取考试用户列表
		log.info("自动考试开始：{}", exam.getName());
		for (MyExam myExam : myExamList) {
			User user = userService.getEntity(myExam.getUserId());
			log.info("自动考试进行：{}-{}开始", user.getId(), user.getName());
		
			// 获取考试用户的试卷
			List<MyExamDetail> userAnswerList = getUserAnswerList(examId, myExam.getUserId());
			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			if (paper.getMarkType() == 1) {// 如果是智能阅卷，记录阅卷开始时间；
				myExam.setMarkStartTime(new Date());
			}
			
			// 开始阅卷
			if (myExam.getState() == 1) {// 如果未考试，状态还是未考试
				log.info("自动考试进行：{}-{}未考试，得0分，不及格", user.getId(), user.getName());
			} else if (myExam.getState() == 2) {// 如果是考试中，标记为交卷
				myExam.setState(3);
				log.info("自动考试进行：{}-{}未交卷，标记为已交卷", user.getId(), user.getName());
			}
			for (MyExamDetail userAnswer : userAnswerList) {
				Question question = questionCache.get(userAnswer.getQuestionId());
				List<PaperQuestionAnswer> questionAnswerList = questionAnswerListCache.get(userAnswer.getQuestionId());
				PaperQuestion questionOption = questionOptionCache.get(userAnswer.getQuestionId());
				if (hasAi(question)) {// 如果是智能题
					if (hasQA(question)) { 
						qAHandle(question, questionOption, questionAnswerList, userAnswer);// 问答处理
					} else if (hasSingleChoice(question) || hasTrueFalse(question)) {
						singleChoiceHandle(question, questionOption, questionAnswerList, userAnswer);// 单选判断处理
					} else if (hasMultipleChoice(question)) { 
						multipleChoiceHandle(question, questionOption, questionAnswerList, userAnswer);// 多选处理
					} else if (hasFillBlank(question)) { 
						fillBlankHandle(question, questionOption, questionAnswerList, userAnswer);// 填空处理
					}
				
					totalScore.add(userAnswer.getScore());// 累加当前分数到总分数
					myExamDetailService.update(userAnswer);// 更新每道题的分数（没作答也都标记为0分。影响的地方为人工阅卷时，所有题都有分数，才允许阅卷完成。）
				}
			}
			
			// 完成阅卷
			if (paper.getMarkType() == 1 || myExam.getState() == 1) {// 如果是智能阅卷或未考试，直接出成绩
				myExam.setMarkUserId(1);// 阅卷人为admin
				myExam.setMarkEndTime(new Date());// 记录阅卷结束时间
				myExam.setMarkState(3);// 标记为阅卷结束
				myExam.setTotalScore(totalScore.getResult());// 记录成绩 
				BigDecimal passScore = BigDecimalUtil.newInstance(paper.getTotalScore())
						.mul(paper.getPassScore()).div(100, 2).getResult();
				if (BigDecimalUtil.newInstance(totalScore.getResult()).sub(passScore).getResult().doubleValue() >= 0) {
					myExam.setAnswerState(1);// 标记为及格
				} else {
					myExam.setAnswerState(2);// 标记为不及格
				}
			}
			
			myExamService.update(myExam);
			log.info("自动考试进行：{}-{}完成阅卷，自动考试部分得{}分", user.getId(), user.getName(), totalScore.getResult());
		}
		
		// 如果试卷是智能阅卷类型，更新用户排名
		if (paper.getMarkType() == 1) {
			rank(myExamList);
			
			// 完成考试
			exam.setMarkState(3);
			examService.update(exam);
			log.info("自动考试完成：标记考试为已阅卷，结束");
			return;
		}
		
		// 如果试卷是人工阅卷类型，标记考试为阅卷中，等待人工阅卷。（阅卷人在开始阅卷时，如果考试状态为未阅卷则不能阅卷，因为自动考试还未完成。）
		if (paper.getMarkType() == 2) {
			exam.setMarkState(2);
			examService.update(exam);
			log.info("自动考试完成：标记考试为阅卷中，等待人工阅卷");
			
			// 加入完成阅卷监听，保证自动考试完成，才能进行自动阅卷完成
			AutoMarkCache.put(examId, exam);
		}
	}

	@Override
	public void doMark(Integer examId) {
		// 校验数据有效性
		Exam exam = examService.getEntity(examId);
		log.info("完成阅卷校验：{}", exam.getName());
		
		if (exam.getState() == 0) {
			log.error("完成阅卷异常：{}已删除", exam.getName());
			throw new MyException("已删除");
		}
		if (exam.getState() == 2) {
			log.error("完成阅卷异常：{}未发布", exam.getName());
			throw new MyException("未发布");
		}
		if (exam.getState() == 3) {
			log.error("完成阅卷异常：{}已归档", exam.getName());
			throw new MyException("已归档");
		}
		if (exam.getMarkState() == 1) {// 如果状态不是阅卷中，表示自动考试任务错误
			log.error("自动阅卷异常：{}智能题未阅卷", exam.getName());
			throw new MyException("智能题未阅卷");
		}
		if (exam.getMarkState() == 3) {
			log.error("自动阅卷异常：{}已阅卷", exam.getName());
			throw new MyException("已阅卷");
		}
		
		long curTime = System.currentTimeMillis();
		if (exam.getEndTime().getTime() > curTime) {
			log.error("完成阅卷异常：{}考试未结束", exam.getName());
			throw new MyException("考试未结束");
		}
		if (exam.getMarkEndTime().getTime() > curTime) {
			log.error("完成阅卷异常：{}阅卷未结束", exam.getName());
			throw new MyException("阅卷未结束");
		}
		
		// 延时2秒在完成阅卷（阅卷时预留了1秒网络延时，这里在延时1秒，保证都是人工阅卷完成后的结果）
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			log.error("完成阅卷异常：{}，延时执行异常", exam.getName());
		}
		
		// 获取考试用户列表
		log.info("完成阅卷开始：{}", exam.getName());
		Paper paper = paperService.getEntity(exam.getPaperId());// 试卷信息
		List<MyExam> myExamList = myExamService.getList(examId);// 考试用户列表
		for(MyExam myExam : myExamList){
			User user = userService.getEntity(myExam.getUserId());
			if (myExam.getMarkState() == 3) {//已阅卷的不处理（没考试的人考试时间结束已自动阅卷；人工阅卷在阅卷时间结束之前已经（部分）阅完）
				log.info("完成考试进行：{}-{}{}，不处理", user.getId(), user.getName(), myExam.getState() == 1 ? "未考试" : "已阅卷");
				continue;
			}
			
			// 开始补全未处理的阅卷数据，并合计成绩
			if (myExam.getMarkStartTime() == null) {// 一道题也没有阅
				myExam.setMarkUserId(1);//记录阅卷人为admin
				myExam.setMarkStartTime(new Date());// 阅卷时间为当前时间
			}
			
			List<MyExamDetail> userAnswerList = getUserAnswerList(myExam.getExamId(), myExam.getUserId());//计算总分
			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			for (MyExamDetail userAnswer : userAnswerList) {
				if (userAnswer.getScore() == null) {//当阅卷人没有阅卷或部分未阅卷时，阅卷时间到。
					userAnswer.setScore(BigDecimal.ZERO);// 标记该题为0分
					userAnswer.setMarkUserId(1);// 阅卷人admin
					userAnswer.setMarkTime(new Date());// 阅卷时间为当前时间
				    update(userAnswer);
				}
				
				totalScore.add(userAnswer.getScore());// 累加所有题分数到总分数
			}
			
			myExam.setTotalScore(totalScore.getResult());// 记录成绩 
			BigDecimal passScore = BigDecimalUtil.newInstance(paper.getTotalScore()).mul(paper.getPassScore()).div(100, 2).getResult();
			if (BigDecimalUtil.newInstance(totalScore.getResult()).sub(passScore).getResult().doubleValue() >= 0) {
				myExam.setAnswerState(1);// 标记为及格
			} else {
				myExam.setAnswerState(2);// 标记为不及格
			}
			myExam.setMarkState(3);// 标记为阅卷结束
			myExam.setMarkEndTime(new Date());// 标记阅卷结束时间
			
			myExamService.update(myExam);
			log.info("完成考试进行：{}-{}完成阅卷，考试得{}分", user.getId(), user.getName(), totalScore.getResult());
		}
		
		// 更新用户排名
		rank(myExamList);
		
		// 完成考试
		exam.setMarkState(3);
		examService.update(exam);
		log.info("完成阅卷结束：标记考试为已阅卷，结束");
	}

	/**
	 * 用户排名
	 * 
	 * v1.0 zhanghc 2022年1月26日下午1:55:49
	 * @param myExamList void
	 */
	private void rank(List<MyExam> myExamList) {
		Collections.sort(myExamList, new Comparator<MyExam>() {
			@Override
			public int compare(MyExam o1, MyExam o2) {
				if (o2.getTotalScore().compareTo(o1.getTotalScore()) == 0 
						&& o1.getAnswerEndTime() != null && o2.getAnswerEndTime() != null) {
					return Integer.parseInt(String.valueOf(o1.getAnswerEndTime().getTime() - o2.getAnswerEndTime().getTime())); 
				}
				
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
	 * @param questionOption 试题选项
	 * @param questionAnswerList 试题答案
	 * @param userAnswer 用户答案
	 * void
	 */
	private void qAHandle(Question question, PaperQuestion questionOption, 
			List<PaperQuestionAnswer> questionAnswerList, MyExamDetail userAnswer) {
		if (question.getAi() == 2) {// 试题类型为人工阅卷，不处理
			return;
		}
		
		userAnswer.setScore(BigDecimal.ZERO);// 先初始化，防止多次调用分数累加
		if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果用户没有作答，不处理
			return;
		}
		
		boolean dxxbmg = dxxbmg(questionOption);// 大小写不敏感
		String _userAnswer = dxxbmg
				? userAnswer.getAnswer().toLowerCase()
				: userAnswer.getAnswer();// 获取用户答案
		for (PaperQuestionAnswer questionAnswer : questionAnswerList) {// 获取试题某一项关键词
			String[] _questionAnswers = dxxbmg 
					? questionAnswer.getAnswer().toLowerCase().split("\n") 
					: questionAnswer.getAnswer().split("\n");// 获取关键词的所有同义词
			for (String _questionAnswer : _questionAnswers) {// 用户答案和同义词对比
				if (_userAnswer.contains(_questionAnswer)) {// 如果找到（对比条件不要反，用户答案是大段文字）
					userAnswer.setScore(BigDecimalUtil.newInstance(userAnswer.getScore())
							.add(questionAnswer.getScore()).getResult());// 累计该关键词的分数
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
	 * @param questionOption
	 * @param questionAnswerList 
	 * @param userAnswer 
	 * void
	 */
	private void singleChoiceHandle(Question question, PaperQuestion questionOption, 
			List<PaperQuestionAnswer> questionAnswerList, MyExamDetail userAnswer) {
		if (question.getAi() == 2) {// 试题类型为人工阅卷，不处理
			return;
		}
		
		userAnswer.setScore(BigDecimal.ZERO);// 先初始化，防止多次调用分数累加
		if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果用户没有作答，不处理
			return;
		}
		
		for (PaperQuestionAnswer questionAnswer : questionAnswerList) {// 就一个答案，可以不循环
			if (questionAnswer.getAnswer().equals(userAnswer.getAnswer())) {
				userAnswer.setScore(questionAnswer.getScore());
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
	private void multipleChoiceHandle(Question question, PaperQuestion questionOption, 
			List<PaperQuestionAnswer> questionAnswerList, MyExamDetail userAnswer) {
		if (question.getAi() == 2) {// 试题类型为人工阅卷，不处理
			return;
		}
		
		userAnswer.setScore(BigDecimal.ZERO);// 先初始化，防止多次调用分数累加
		if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果用户没有作答，不处理
			return;
		}
		
		Set<String> userAnswerSet = new HashSet<String>(Arrays.asList(userAnswer.getAnswer().split(",")));// 获取用户答案
		Set<String> questionAnswerSet = new HashSet<String>();// 获取试题答案
		for(PaperQuestionAnswer questionAnswer : questionAnswerList){
			String[] questionAnswerSplit = questionAnswer.getAnswer().split(",");
			for (int i = 0; i < questionAnswerSplit.length; i++) {
				questionAnswerSet.add(questionAnswerSplit[i]);
			}
		}
		
		if (questionAnswerSet.size() == userAnswerSet.size() && questionAnswerSet.containsAll(userAnswerSet)) {// 如果完全正确，得满分
			userAnswer.setScore(questionOption.getScore());
		} else if (lxdf(questionOption) && questionAnswerSet.containsAll(userAnswerSet)) {// 如果勾选了漏选得分，得漏选的分
			userAnswer.setScore(questionAnswerList.get(0).getScore());
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
	private void fillBlankHandle(Question question, PaperQuestion questionOption, 
			List<PaperQuestionAnswer> questionAnswerList, MyExamDetail userAnswer) {
		if (question.getAi() == 2) {// 试题类型为人工阅卷，不处理
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
		boolean dxxbmg = dxxbmg(questionOption);// 大小写不敏感
		String[] userAnswers = dxxbmg
				? userAnswer.getAnswer().toLowerCase().split("\n")
				: userAnswer.getAnswer().split("\n");// 获取用户答案（多空就是多个答案）
		for (int i = 0; i < userAnswers.length; i++) {// 循环用户每一项答案
			for (int j = 0; j < questionAnswerList.size(); j++) {// 循环每一项试题关键词
				PaperQuestionAnswer questionAnswer = questionAnswerList.get(j);
				String[] synonyms = dxxbmg 
						? questionAnswer.getAnswer().toLowerCase().split("\n") 
						: questionAnswer.getAnswer().split("\n");// 获取关键词的所有同义词
						
				if (!dawsx(questionOption)) {// 如果勾选了答案前后有顺序，则对应位置对比
					if (i != j) {// 不是对应位置，返回继续查找
						continue;
					}
				}
				
				for (String synonym : synonyms) {// 循环每一项同义词
					if (userAnswers[i].equals(synonym)) {// 如果用户某一空答案，匹配某一项关键词的同义词
						userAnswer.setScore(BigDecimalUtil.newInstance(userAnswer.getScore())
								.add(questionAnswer.getScore()).getResult());// 累计该关键词的分数
						break;// 匹配到一个同义词就结束；返回大循环继续对比其他关键词
					}
				}
			}
		}
	}

	/**
	 * 是否智能判卷
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	private boolean hasAi(Question question) {
		return question.getAi() == 1;
	}
	
	/**
	 * 是否单选题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	private boolean hasSingleChoice(Question question) {
		return question.getType() == 1;
	}
	
	/**
	 * 是否多选题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	private boolean hasMultipleChoice(Question question) {
		return question.getType() == 2;
	}
	
	/**
	 * 是否填空题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	private boolean hasFillBlank(Question question) {
		return question.getType() == 3;
	}

	/**
	 * 是否判断题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	private boolean hasTrueFalse(Question question) {
		return question.getType() == 4;
	}

	/**
	 * 是否问答题
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:40:37
	 * @param question
	 * @return boolean
	 */
	private boolean hasQA(Question question) {
		return question.getType() == 5;
	}

	/**
	 * 3：大小写不敏感（默认大小写敏感）；
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:02
	 * @param paperQuestionEx
	 * @return boolean
	 */
	private boolean dxxbmg(PaperQuestion paperQuestion) {
		return ValidateUtil.isValid(paperQuestion.getScoreOptions()) 
				&& paperQuestion.getScoreOptions().contains("3");
	}
	
	/**
	 * 1：漏选得分（默认全对得分）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:10
	 * @param paperQuestionEx
	 * @return boolean
	 */
	private boolean lxdf(PaperQuestion paperQuestion) {
		return ValidateUtil.isValid(paperQuestion.getScoreOptions()) 
				&& paperQuestion.getScoreOptions().contains("1");
	}

	/**
	 * 2：答案无顺序（默认答案有前后顺序）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:06:24
	 * @param paperQuestionEx
	 * @return boolean
	 */
	private boolean dawsx(PaperQuestion paperQuestion) {
		return ValidateUtil.isValid(paperQuestion.getScoreOptions()) 
				&& paperQuestion.getScoreOptions().contains("2");
	}
	
	/**
	 * 获取试题缓存
	 * 
	 * v1.0 zhanghc 2021年10月22日下午1:22:13
	 * @param paperId
	 * @return Map<Integer,Question>
	 */
	private Map<Integer, Question> getQuestionCache(Integer paperId) {
		List<Question> questionList = paperService.getQuestionList(paperId);
		Map<Integer, Question> questionCache = new HashMap<>();
		for (Question question : questionList) {
			questionCache.put(question.getId(), question);
		}
		return questionCache;
	}
	
	/**
	 * 获取随机试题缓存
	 * 
	 * v1.0 chenyun 2022年2月16日下午2:10:27
	 * @param paperId
	 * @return Map<Integer,Question>
	 */
	private Map<Integer, Question> getQuestionRandCache(Integer paperId, Integer examId) {
		Map<Integer, Question> questionCache = new HashMap<>();
		List<Question> questionList = paperQuestionService.getQuestionRandList(examId, paperId);
		for (Question question : questionList) {
			questionCache.put(question.getId(), question);
		}
		return questionCache;
	}
	
	/**
	 * 获取试题答案缓存
	 * 
	 * v1.0 zhanghc 2021年10月22日下午1:32:06
	 * @param paperId
	 * @param questionList
	 * @return Map<Integer,List<PaperQuestionAnswer>>
	 */
	private Map<Integer, List<PaperQuestionAnswer>> questionAnswerListCache(Integer paperId, Collection<Question> questionList) {
		Map<Integer, List<PaperQuestionAnswer>> questionAnswerListCache = new HashMap<>();
		for (Question question : questionList) {
			List<PaperQuestionAnswer> questionAnswerList = paperQuestionAnswerService.getList(paperId, question.getId());
			questionAnswerListCache.put(question.getId(), questionAnswerList);
		}
		return questionAnswerListCache;
	}
	
	/**
	 * 获取随机试题答案缓存
	 * 
	 * v1.0 chenyun 2022年2月16日下午2:55:06
	 * @param paperId
	 * @param questionRandList
	 * @return Map<Integer,List<PaperQuestionAnswer>>
	 */
	private Map<Integer, List<PaperQuestionAnswer>> questionRandAnswerListCache(Integer examId, Integer paperId, Collection<Question> questionRandList) {
		Map<Integer, List<PaperQuestionAnswer>> questionAnswerListCache = new HashMap<>();
		for (Question question : questionRandList) {
			List<PaperQuestionAnswer> questionAnswerList = paperQuestionAnswerService.getList(paperId, question.getId());
			questionAnswerListCache.put(question.getId(), questionAnswerList);
		}
		return questionAnswerListCache;
	}
	
	/**
	 * 获取试题选项缓存
	 * 
	 * v1.0 zhanghc 2021年10月22日下午1:32:26
	 * @param paperId
	 * @return Map<Integer,List<PaperQuestionAnswer>>
	 */
	private Map<Integer, PaperQuestion> questionOptionCache(Integer paperId) {
		List<PaperQuestion> paperQuestionList = paperQuestionService.getList(paperId);
		Map<Integer, PaperQuestion> questionOptionCache = new HashMap<>();
		for (PaperQuestion paperQuestion : paperQuestionList) {
			if (paperQuestion.getType() == 1) {
				continue;
			}
			questionOptionCache.put(paperQuestion.getQuestionId(), paperQuestion);
		}
		return questionOptionCache;
	}

	/**
	 * 获取随机试题选项缓存
	 * 
	 * v1.0 chenyun 2022年2月16日下午3:40:08
	 * @param paperId
	 * @return Map<Integer,PaperQuestion>
	 */
	private Map<Integer, PaperQuestion> questionRandOptionCache(Integer examId, Integer paperId) {
		List<PaperQuestion> paperQuestionList = paperQuestionService.getPaperQuestionList(examId, paperId);
		Map<Integer, PaperQuestion> questionOptionCache = new HashMap<>();
		for (PaperQuestion paperQuestion : paperQuestionList) {
			questionOptionCache.put(paperQuestion.getQuestionId(), paperQuestion);
		}
		return questionOptionCache;
	}
	
	@Override
	public void del(Integer examId, Integer userId) {
		myExamDetailDao.del(examId, userId);
	}
}
