package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.wcpdoc.exam.base.cache.ProgressBarCache;
import com.wcpdoc.exam.base.entity.User;
import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.dao.BaseDao;
import com.wcpdoc.exam.core.dao.MyExamDetailDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyExamDetail;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamDetailService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

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
	private MyMarkService myMarkService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private UserService userService;
	@Resource
	private QuestionAnswerService questionAnswerService;

	@Override
	@Resource(name = "myExamDetailDaoImpl")
	public void setDao(BaseDao<MyExamDetail> dao) {
		super.dao = dao;
	}

	@Override
	public List<MyExamDetail> getList(Integer myExamId) {
		return myExamDetailDao.getList(myExamId);
	}

	@Override
	public List<Map<String, Object>> getAnswerList(Integer myExamId) {
		return myExamDetailDao.getAnswerList(myExamId, getCurUser().getId());
	}
	
	@Override
	public void delByMyExamId(Integer myExamId) {
		myExamDetailDao.delByMyExamId(myExamId);
	}

	@Override
	public void autoMark(Integer examId, LoginUser curUser, String processBarId) {
		// 校验数据有效性
		Exam exam = examService.getEntity(examId);
		log.info("自动阅卷校验：{}", exam.getName());
		if (exam.getState() == 0) {
			log.error("自动阅卷异常：考试已删除！");
			throw new MyException("考试已删除！");
		}
		if (exam.getState() == 2) {
			log.error("自动阅卷异常：考试未发布！");
			throw new MyException("考试未发布！");
		}
		if (exam.getMarkStartTime().getTime() > (new Date().getTime() - 30000)){//预留30秒网络延时
			log.error("自动阅卷异常：阅卷未开始！");
			throw new MyException("阅卷未开始！");
		}
		if (exam.getMarkEndTime().getTime() < (new Date().getTime() - 30000)){//预留30秒网络延时
			log.error("自动阅卷异常：阅卷已结束！");
			throw new MyException("阅卷已结束！");
		}
		List<MyMark> myMarkList = myMarkService.getList(examId);
		boolean hasMyMark = false;
		for (MyMark myMark : myMarkList) {
			if (myMark.getMarkUserId().intValue() == curUser.getId().intValue()) {
				hasMyMark = true;
				break;
			}
		}
		if (!hasMyMark) {
			log.error("自动阅卷异常：未参与阅卷：{}", exam.getName());
			throw new MyException(String.format("未参与阅卷：%s", exam.getName()));
		}

		// 开始自动阅卷
		log.info("自动阅卷开始：");
		List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(exam.getPaperId());// 获取试卷信息
		List<MyExam> myExamList = myExamService.getList(examId);// 获取用户信息
		double myExamCount = myExamList.size();
		ProgressBarCache.setProgressBar(processBarId, 0.0, myExamCount, null);
		boolean hasQA = false;
		for (int i = 0; i < myExamList.size(); i++) {
			MyExam myExam = myExamList.get(i);
			Map<Long, MyExamDetail> userAnswerMap = getUserAnswer(myExam);// 获取用户答案信息
			User user = userService.getEntity(myExam.getUserId());
			log.info("自动阅卷进行：{}-{}开始", user.getId(), user.getName());

			if (hasExaming(myExam)) {// 如果是考试中，标记为强制交卷（未考试不管）
				myExam.setState(4);
				myExamService.update(myExam);
				log.info("自动阅卷进行：{}-{}未交卷，标记为强制交卷", user.getId(), user.getName());
			}

			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			for (PaperQuestionEx paperQuestionEx : paperQuestionExList) {// 第一层是章节
				for (PaperQuestionEx questionAnswer : paperQuestionEx.getSubList()) {// 第二层是试题
					Question question = questionAnswer.getQuestion();
					if (hasAi(question)) {//是否智能判卷
						continue;
					} else {
						hasQA = true;
					}
					MyExamDetail userAnswer = userAnswerMap.get(question.getId() + 0L);// 获取当前试题对应的用户答案
					if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果未作答
						continue;
					}
					
					/*if (!hasQA(question)) {// 非问答题
						userAnswer.setScore(BigDecimal.ZERO);// 默认设置为0分（为简化代码，以下直接set不用update更新，hibernate会同步到数据库）
					}*/
					userAnswer.setScore(BigDecimal.ZERO);// 默认设置为0分（为简化代码，以下直接set不用update更新，hibernate会同步到数据库）
					
					if (hasQA(question)) { // 问答题处理
						qAHandle(questionAnswer, question, userAnswer);
					} else if (hasSingleChoice(question) || hasTrueFalse(question)) { // 单选判断处理
						singleChoiceHandle(questionAnswer, question, userAnswer);
					} else if (hasMultipleChoice(question)) { // 多选处理
						multipleChoiceHandle(questionAnswer, question, userAnswer);
					} else if (hasFillBlank(question)) { // 填空处理
						fillBlankHandle(questionAnswer, question, userAnswer);
					}
					
					/*if (!hasQA(question)) {
						totalScore.add(userAnswer.getScore());//合计总分数
					}*/
					totalScore.add(userAnswer.getScore());//合计总分数
				}
			}
			
			// 标记为阅卷中
			myExam.setMyMarkId(curUser.getId());
			String msg = null;
			Date curTime = new Date();
			myExam.setMarkStartTime(curTime);
			if (!hasQA) {//如果没有人工阅卷，表示阅卷完成
				myExam.setMarkState(3);
				myExam.setAnswerEndTime(curTime);
				myExam.setTotalScore(totalScore.getResult());
				msg = "自动阅卷完成";
			} else {//否则表示阅卷中，等待人工阅卷
				myExam.setMarkState(2);
				msg = "自动阅卷部分已完成，问答题请人工阅卷！";
			}
			myExamService.update(myExam);
			ProgressBarCache.setProgressBar(processBarId, i + 1.0, myExamCount, msg);
			log.info("自动阅卷进行：进度{}", ProgressBarCache.getProgressBar(processBarId).getPercent());
		}
		log.info("自动阅卷结束：");
	}

	/**
	 * 问答处理
	 * 
	 * v1.0 chenyun 2021年7月21日下午2:09:40
	 * @param questionAnswer
	 * @param question
	 * @param userAnswer void
	 */
	private void qAHandle(PaperQuestionEx questionAnswer, Question question, MyExamDetail userAnswer) {
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		List<String> questionAnswerStringList = new ArrayList<String>();
		BigDecimal scoreSum = new BigDecimal(0.00);
		List<BigDecimal> questionAnswerBigDecimalList = new ArrayList<BigDecimal>();
		String userAnswerStr = userAnswer.getAnswer();
		if (dxxbmg(questionAnswer)) {// 如果勾选了大小写不敏感，则全部大写转换一次在处理
			for(QuestionAnswer questionAnswerEntity : questionAnswerList){
				questionAnswerStringList.add(questionAnswerEntity.getAnswer().toUpperCase());		//大小写不敏感
				questionAnswerBigDecimalList.add(questionAnswerEntity.getScore());					//分值
			}
			userAnswerStr = userAnswerStr.toUpperCase();
		}else{
			for(QuestionAnswer questionAnswerEntity : questionAnswerList){
				questionAnswerStringList.add(questionAnswerEntity.getAnswer());
				questionAnswerBigDecimalList.add(questionAnswerEntity.getScore());
			}
		}
		BigDecimal[] scoreArr = questionAnswerBigDecimalList.toArray(new BigDecimal[questionAnswerBigDecimalList.size()]);
		String[] questionAnswerArr =  questionAnswerStringList.toArray(new String[questionAnswerStringList.size()]);// 试题答案：一般|||通常|||普遍\njava|||.net
		String[] userAnswerArr = userAnswerStr.split("\n");// 用户答案：一般情况下\nJava
		boolean[] userFillBlanksArr = new boolean[questionAnswerArr.length];// 添加用户每个填空是否正确
		
		for (int i = 0; i < questionAnswerArr.length; i++) {// 答案对比
			String[] _questionAnswerArr = questionAnswerArr[i].split("\\|\\|\\|");// 答案有多个同义词
			for (int j = 0; j < userAnswerArr.length; j++) {
				for (String _questionAnswer : _questionAnswerArr) {// 用户答案和试题答案对比
					if (bhdadf(questionAnswer)) {
						if (userAnswerArr[j].contains(_questionAnswer)) {
							userFillBlanksArr[i] = true;
							scoreSum = scoreSum.add(scoreArr[i]);
							break;
						}
					}
				}
			}
		}

		int trueNum = 0;
		for (boolean b : userFillBlanksArr) {
			if (b) {
				trueNum++;
			}
		}

		if (userFillBlanksArr.length == trueNum) {// 默认全对得分
			userAnswer.setScore(questionAnswer.getScore());
			return;
		}
		userAnswer.setScore(scoreSum);
	}

	/**
	 * 填空处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:11:55
	 * @param questionAnswer
	 * @param question
	 * @param userAnswer void
	 */
	private void fillBlankHandle(PaperQuestionEx questionAnswer, Question question, MyExamDetail userAnswer) {
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		List<String> questionAnswerStringList = new ArrayList<String>();
		BigDecimal scoreSum = new BigDecimal(0.00);
		List<BigDecimal> questionAnswerBigDecimalList = new ArrayList<BigDecimal>();
		String userAnswerStr = userAnswer.getAnswer();
		if (dxxbmg(questionAnswer)) {// 如果勾选了大小写不敏感，则全部大写转换一次在处理
			for(QuestionAnswer questionAnswerEntity : questionAnswerList){
				questionAnswerStringList.add(questionAnswerEntity.getAnswer().toUpperCase());		//大小写不敏感
				questionAnswerBigDecimalList.add(questionAnswerEntity.getScore());					//分值
			}
			userAnswerStr = userAnswerStr.toUpperCase();
		}else{
			for(QuestionAnswer questionAnswerEntity : questionAnswerList){
				questionAnswerStringList.add(questionAnswerEntity.getAnswer());
				questionAnswerBigDecimalList.add(questionAnswerEntity.getScore());
			}
		}
		BigDecimal[] scoreArr = questionAnswerBigDecimalList.toArray(new BigDecimal[questionAnswerBigDecimalList.size()]);
		String[] questionAnswerArr =  questionAnswerStringList.toArray(new String[questionAnswerStringList.size()]);// 试题答案：一般|||通常|||普遍\njava|||.net
		String[] userAnswerArr = userAnswerStr.split("\n");// 用户答案：一般情况下\nJava
		boolean[] userFillBlanksArr = new boolean[questionAnswerArr.length];// 添加用户每个填空是否正确
		
		for (int i = 0; i < questionAnswerArr.length; i++) {// 答案对比
			String[] _questionAnswerArr = questionAnswerArr[i].split("\\|\\|\\|");// 答案有多个同义词
			for (int j = 0; j < userAnswerArr.length; j++) {
				if (!dawsx(questionAnswer)) {// 如果勾选了答案前后有顺序，则对应位置对比
					if (i != j) {
						continue;
					}
				}

				for (String _questionAnswer : _questionAnswerArr) {// 用户答案和试题答案对比
					if (userAnswerArr[j].equals(_questionAnswer)) {// 默认等于答案得分，userAnswerStrArr[j].equals(_questionAnswer)位置不要反
						userFillBlanksArr[i] = true;
						scoreSum = scoreSum.add(scoreArr[i]);
						break;
					}

					if (bhdadf(questionAnswer)) {// 如果勾选了用户答案包含试题答案，则包含得分
						if (userAnswerArr[j].contains(_questionAnswer)) {
							userFillBlanksArr[i] = true;
							scoreSum = scoreSum.add(scoreArr[i]);
							break;
						}
					}
				}
			}
		}

		int trueNum = 0;
		for (boolean b : userFillBlanksArr) {
			if (b) {
				trueNum++;
			}
		}

		if (userFillBlanksArr.length == trueNum) {// 默认全对得分
			userAnswer.setScore(questionAnswer.getScore());
			return;
		}

		if (bdbf(questionAnswer)) {// 如果勾选了漏选的分，则漏选的分
			if (trueNum > 0) {
				//userAnswer.setScore(BigDecimalUtil.newInstance(questionAnswer.getScore()).div(2, 2).getResult());
				userAnswer.setScore(scoreSum);
			}
		}
	}

	/**
	 * 多选处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:11:47
	 * @param questionAnswer
	 * @param question
	 * @param userAnswer void
	 */
	private void multipleChoiceHandle(PaperQuestionEx questionAnswer, Question question, MyExamDetail userAnswer) {
		Set<String> userAnswerSet = new HashSet<String>(Arrays.asList(userAnswer.getAnswer().split(",")));
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		Set<String> questionAnswerSet = new HashSet<String>();
		BigDecimal setScore = new BigDecimal(0.00);
		for(QuestionAnswer questionAnswerEntity : questionAnswerList){
			questionAnswerSet.add(questionAnswerEntity.getAnswer());
			if (userAnswerSet.contains(questionAnswerEntity.getAnswer())) {//如果包含答案，加答案指定分数
				setScore = setScore.add(questionAnswerEntity.getScore());
			}
		}
		if (questionAnswerSet.size() == userAnswerSet.size() && questionAnswerSet.containsAll(userAnswerSet)) { // 全对得分
			userAnswer.setScore(questionAnswer.getScore());
			return;
		}

		if (bdbf(questionAnswer)) { // 如果勾选了漏选的分，则漏选的分
			if (questionAnswerSet.containsAll(userAnswerSet)) {
				//userAnswer.setScore(BigDecimalUtil.newInstance(questionAnswer.getScore()).div(2, 2).getResult());
				userAnswer.setScore(setScore);   //选对的分数和
			}
		}
	}

	/**
	 * 单选处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:06:10
	 * @param questionAnswer
	 * @param question
	 * @param userAnswer void
	 */
	private void singleChoiceHandle(PaperQuestionEx questionAnswer, Question question, MyExamDetail userAnswer) {
		List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
		if (questionAnswerList.get(0).getAnswer().equals(userAnswer.getAnswer())) {// 全对得分
			userAnswer.setScore(questionAnswer.getScore());
		}
	}

	/**
	 * 是否考试中
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:44:01
	 * @param myExam
	 * @return boolean
	 */
	private boolean hasExaming(MyExam myExam) {
		return myExam.getState() == 2;
	}

	/**
	 * 是否只能判卷
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
	 * 获取用户答案
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:28:48
	 * @param myExam
	 * @return Map<Long,MyExamDetail>
	 */
	private Map<Long, MyExamDetail> getUserAnswer(MyExam myExam) {
		List<MyExamDetail> myExamDetailList = getList(myExam.getId());
		Map<Long, MyExamDetail> myExamDetailMap = new HashMap<Long, MyExamDetail>();
		for (MyExamDetail myExamDetail : myExamDetailList) {
			myExamDetailMap.put(myExamDetail.getQuestionId().longValue(), myExamDetail);
		}
		return myExamDetailMap;
	}
	
	/**
	 * 4：用户答案包含试题答案（默认等于答案得分）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:05:51
	 * @param paperQuestionEx
	 * @return boolean
	 */
	private boolean bhdadf(PaperQuestionEx paperQuestionEx) {
		return ValidateUtil.isValid(paperQuestionEx.getScoreOptions()) 
				&& paperQuestionEx.getScoreOptions().contains("4");
	}

	/**
	 * 3：大小写不敏感（默认大小写敏感）；
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:02
	 * @param paperQuestionEx
	 * @return boolean
	 */
	private boolean dxxbmg(PaperQuestionEx paperQuestionEx) {
		return ValidateUtil.isValid(paperQuestionEx.getScoreOptions()) 
				&& paperQuestionEx.getScoreOptions().contains("3");
	}

	/**
	 * 2：答案无顺序（默认答案有前后顺序）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:06:24
	 * @param paperQuestionEx
	 * @return boolean
	 */
	private boolean dawsx(PaperQuestionEx paperQuestionEx) {
		return ValidateUtil.isValid(paperQuestionEx.getScoreOptions()) 
				&& paperQuestionEx.getScoreOptions().contains("2");
	}
	
	/**
	 * 1：漏选的分（默认全对得分）
	 * 
	 * v1.0 zhanghc 2018年11月14日下午11:07:10
	 * @param paperQuestionEx
	 * @return boolean
	 */
	private boolean bdbf(PaperQuestionEx paperQuestionEx) {
		return ValidateUtil.isValid(paperQuestionEx.getScoreOptions()) 
				&& paperQuestionEx.getScoreOptions().contains("1");
	}
}
