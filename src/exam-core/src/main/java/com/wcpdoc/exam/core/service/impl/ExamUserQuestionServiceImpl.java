package com.wcpdoc.exam.core.service.impl;

import java.math.BigDecimal;
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
import com.wcpdoc.exam.core.dao.ExamUserQuestionDao;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamUser;
import com.wcpdoc.exam.core.entity.ExamUserQuestion;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.MarkUser;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.ExamUserQuestionService;
import com.wcpdoc.exam.core.service.ExamUserService;
import com.wcpdoc.exam.core.service.MarkUserService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.util.BigDecimalUtil;
import com.wcpdoc.exam.core.util.ValidateUtil;

/**
 * 考试用户试题服务层实现
 * 
 * v1.0 zhanghc 2017-06-19 16:28:29
 */
@Service
public class ExamUserQuestionServiceImpl extends BaseServiceImp<ExamUserQuestion> implements ExamUserQuestionService {
	private static final Logger log = LoggerFactory.getLogger(ExamUserQuestionServiceImpl.class);
	@Resource
	private ExamUserQuestionDao examUserQuestionDao;
	@Resource
	private ExamService examService;
	@Resource
	private PaperService paperService;
	@Resource
	private MarkUserService markUserService;
	@Resource
	private ExamUserService examUserService;
	@Resource
	private UserService userService;

	@Override
	@Resource(name = "examUserQuestionDaoImpl")
	public void setDao(BaseDao<ExamUserQuestion> dao) {
		super.dao = dao;
	}

	@Override
	public List<ExamUserQuestion> getList(Integer examUserId) {
		return examUserQuestionDao.getList(examUserId);
	}

	@Override
	public void delByExamUserId(Integer examUserId) {
		examUserQuestionDao.delByExamUserId(examUserId);
	}

	@Override
	public void doAutoMark(Integer examId, LoginUser curUser, String processBarId) {
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
		Date curTime = new Date();
		if (exam.getMarkStartTime().getTime() > curTime.getTime()) {
			log.error("自动阅卷异常：阅卷未开始！");
			throw new MyException("阅卷未开始！");
		}
		if (exam.getMarkEndTime().getTime() < curTime.getTime()) {
			log.error("自动阅卷异常：阅卷已结束！");
			throw new MyException("阅卷已结束！");
		}
		List<MarkUser> markUserList = markUserService.getList(examId);
		boolean hasMarkUser = false;
		for (MarkUser markUser : markUserList) {
			if (markUser.getUserId().intValue() == curUser.getId().intValue()) {
				hasMarkUser = true;
				break;
			}
		}
		if (!hasMarkUser) {
			log.error("自动阅卷异常：未参与阅卷：{}", exam.getName());
			throw new MyException(String.format("未参与阅卷：%s", exam.getName()));
		}

		// 开始自动阅卷
		log.info("自动阅卷开始：");
		List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(exam.getPaperId());// 获取试卷信息
		List<ExamUser> examUserList = examUserService.getList(examId);// 获取用户信息
		double examUserCount = examUserList.size();
		ProgressBarCache.setProgressBar(processBarId, 0.0, examUserCount, null);
		boolean hasQA = false;
		for (int i = 0; i < examUserList.size(); i++) {
			ExamUser examUser = examUserList.get(i);
			Map<Long, ExamUserQuestion> userAnswerMap = getUserAnswer(examUser);// 获取用户答案信息
			User user = userService.getEntity(examUser.getUserId());
			log.info("自动阅卷进行：{}-{}开始", user.getId(), user.getName());

			if (hasExaming(examUser)) {// 如果是考试中，标记为强制交卷（未考试不管）
				examUser.setState(4);
				examUserService.update(examUser);
				log.info("自动阅卷进行：{}-{}未交卷，标记为强制交卷", user.getId(), user.getName());
			}

			BigDecimalUtil totalScore = BigDecimalUtil.newInstance(0);
			for (PaperQuestionEx paperQuestionEx : paperQuestionExList) {// 第一层是章节
				for (PaperQuestionEx questionAnswer : paperQuestionEx.getSubList()) {// 第二层是试题
					Question question = questionAnswer.getQuestion();
					ExamUserQuestion userAnswer = userAnswerMap.get(question.getId() + 0L);// 获取当前试题对应的用户答案
					userAnswer.setScore(BigDecimal.ZERO);// 默认设置为0分（为简化代码，以下直接set不用update更新，hibernate会同步到数据库）
					
					if (!ValidateUtil.isValid(userAnswer.getAnswer())) {// 如果未作答
						continue;
					}
					
					if (hasQA(question)) { // 问答题处理
						hasQA = true;
						qAHandle(questionAnswer, question, userAnswer);
					} else if (hasSingleChoice(question) || hasTrueFalse(question)) { // 单选判断处理
						singleChoiceHandle(questionAnswer, question, userAnswer);
					} else if (hasMultipleChoice(question)) { // 多选处理
						multipleChoiceHandle(questionAnswer, question, userAnswer);
					} else if (hasFillBlank(question)) { // 填空处理
						fillBlankHandle(questionAnswer, question, userAnswer);
					}
					
					totalScore.add(userAnswer.getScore());
				}
			}
			
			// 标记为阅卷中
			examUser.setMarkUserId(curUser.getId());
			String msg = null;
			examUser.setMarkTime(curTime);
			if (!hasQA) {//如果没有问答题，表示阅卷完成
				examUser.setMarkState(3);
				examUser.setAnswerFinishTime(curTime);
				examUser.setTotalScore(totalScore.getResult());
				msg = "自动阅卷完成";
			} else {//否则表示阅卷中，等待人工阅卷
				examUser.setMarkState(2);
				msg = "自动阅卷部分完成，问答题请人工阅卷。";
			}
			examUserService.update(examUser);
			ProgressBarCache.setProgressBar(processBarId, i + 1.0, examUserCount, msg);
			log.info("自动阅卷进行：进度{}", ProgressBarCache.getProgressBar(processBarId).getPercent());
		}
		log.info("自动阅卷结束：");
	}

	private void qAHandle(PaperQuestionEx questionAnswer, Question question, ExamUserQuestion userAnswer) {
		// 人工阅卷，不处理
	}

	/**
	 * 填空处理
	 * 
	 * v1.0 zhanghc 2020年10月13日下午8:11:55
	 * @param questionAnswer
	 * @param question
	 * @param userAnswer void
	 */
	private void fillBlankHandle(PaperQuestionEx questionAnswer, Question question, ExamUserQuestion userAnswer) {
		String questionAnswerStr = question.getAnswer();
		String userAnswerStr = userAnswer.getAnswer();
		if (dxxbmg(questionAnswer)) {// 如果勾选了大小写不敏感，则全部大写转换一次在处理
			questionAnswerStr = questionAnswerStr.toUpperCase();
			userAnswerStr = userAnswerStr.toUpperCase();
		}

		String[] questionAnswerArr = questionAnswerStr.split("\n");// 试题答案：一般|||通常|||普遍\njava|||.net
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
						break;
					}

					if (bhdadf(questionAnswer)) {// 如果勾选了用户答案包含试题答案，则包含得分
						if (userAnswerArr[j].contains(_questionAnswer)) {
							userFillBlanksArr[i] = true;
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

		if (bdbf(questionAnswer)) {// 如果勾选了半对半分，则半对半分
			if (trueNum > 0) {
				userAnswer.setScore(BigDecimalUtil.newInstance(questionAnswer.getScore()).div(2, 2).getResult());
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
	private void multipleChoiceHandle(PaperQuestionEx questionAnswer, Question question, ExamUserQuestion userAnswer) {
		Set<String> questionAnswerSet = new HashSet<String>(Arrays.asList(question.getAnswer().split(",")));
		Set<String> userAnswerSet = new HashSet<String>(Arrays.asList(userAnswer.getAnswer().split(",")));
		if (questionAnswerSet.size() == userAnswerSet.size() && questionAnswerSet.containsAll(userAnswerSet)) { // 全对得分
			userAnswer.setScore(questionAnswer.getScore());
			return;
		}

		if (bdbf(questionAnswer)) { // 如果勾选了半对半分，则半对半分
			if (questionAnswerSet.containsAll(userAnswerSet)) {
				userAnswer.setScore(BigDecimalUtil.newInstance(questionAnswer.getScore()).div(2, 2).getResult());
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
	private void singleChoiceHandle(PaperQuestionEx questionAnswer, Question question, ExamUserQuestion userAnswer) {
		if (question.getAnswer().equals(userAnswer.getAnswer())) {// 全对得分
			userAnswer.setScore(questionAnswer.getScore());
		}
	}

	/**
	 * 是否考试中
	 * 
	 * v1.0 zhanghc 2020年10月13日下午7:44:01
	 * @param examUser
	 * @return boolean
	 */
	private boolean hasExaming(ExamUser examUser) {
		return examUser.getState() == 2;
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
	 * @param examUser
	 * @return Map<Long,ExamUserQuestion>
	 */
	private Map<Long, ExamUserQuestion> getUserAnswer(ExamUser examUser) {
		List<ExamUserQuestion> examUserQuestionList = getList(examUser.getId());
		Map<Long, ExamUserQuestion> examUserQuestionMap = new HashMap<Long, ExamUserQuestion>();
		for (ExamUserQuestion examUserQuestion : examUserQuestionList) {
			examUserQuestionMap.put(examUserQuestion.getQuestionId().longValue(), examUserQuestion);
		}
		return examUserQuestionMap;
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
	 * 1：半对半分（默认全对得分）
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
