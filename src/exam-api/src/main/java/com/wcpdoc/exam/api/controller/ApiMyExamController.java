package com.wcpdoc.exam.api.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.cache.QuestionCache;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.util.QuestionUtil;

/**
 * 我的考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/myExam")
public class ApiMyExamController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(ApiMyExamController.class);
	
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private ExamService examService;
	
	/**
	 * 我的考试列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = myExamService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				Exam exam = new Exam();
				exam.setScoreState((Integer)map.remove("examScoreState"));// 页面不需要字段用remove
				exam.setMarkState((Integer)map.get("examMarkState"));
				MyExam myExam = new MyExam();
				myExam.setMarkState((Integer)map.get("markState"));
				
				if (!scoreShow(exam, myExam)) {// 成绩查询状态（1：考试结束后；2：不公布；3：交卷后）
					map.put("totalScore", null);// 不显示分数
					map.put("answerState", null);// 不显示及格状态
				}
				if ((Integer)map.remove("examRankState") == 2) {// 排名状态（1：公布；2：不公布）
					map.put("no", null);// 不显示排名
				}
			}
			
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的考试列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取我的考试
	 * 
	 * v1.0 zhanghc 2022年11月2日下午2:38:55
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer examId) {
		try {
			MyExam myExam = myExamService.getMyExam(examId, getCurUser().getId());
			if (myExam == null) {
				throw new MyException("无查阅权限");
			}
			Exam exam = examService.getEntity(examId);
			return PageResultEx.ok()
				.addAttr("examMarkState", exam.getMarkState()) // 页面控制是否显示错题
				.addAttr("examScoreState", exam.getScoreState())// 页面控制是否显示错题
				.addAttr("examRankState", exam.getRankState())// 页面控制是否显示排名
				.addAttr("examStartTime", DateUtil.formatDateTime(exam.getStartTime()))
				.addAttr("examEndTime", DateUtil.formatDateTime(exam.getEndTime()))// 考试结束时间（进入我的试卷使用）
				.addAttr("examMarkStartTime", ValidateUtil.isValid(exam.getMarkStartTime()) ? DateUtil.formatDateTime(exam.getMarkStartTime()) : null)
				.addAttr("examMarkEndTime", ValidateUtil.isValid(exam.getMarkEndTime()) ? DateUtil.formatDateTime(exam.getMarkEndTime()) : null)// 如果是交卷后公布，但试卷是主观题试卷，页面提示几点之后查询
				.addAttr("examName", exam.getName())// 考试名称
				.addAttr("answerStartTime", ValidateUtil.isValid(myExam.getAnswerStartTime()) ? DateUtil.formatDateTime(myExam.getAnswerStartTime()) : null)
				.addAttr("answerEndTime", ValidateUtil.isValid(myExam.getAnswerEndTime()) ? DateUtil.formatDateTime(myExam.getAnswerEndTime()) : null)
				.addAttr("markStartTime", ValidateUtil.isValid(myExam.getMarkStartTime()) ? DateUtil.formatDateTime(myExam.getMarkStartTime()) : null)
				.addAttr("markEndTime", ValidateUtil.isValid(myExam.getMarkEndTime()) ? DateUtil.formatDateTime(myExam.getMarkEndTime()) : null)
				.addAttr("objectiveScore", myExam.getObjectiveScore())
				.addAttr("totalScore", scoreShow(exam, myExam) ? myExam.getTotalScore() : null)
				.addAttr("answerState", scoreShow(exam, myExam) ? myExam.getAnswerState() : null)
				.addAttr("state", myExam.getState())
				.addAttr("markState", myExam.getMarkState())
				.addAttr("no", exam.getRankState() == 1 ? myExam.getNo() : null)
				.addAttr("userNum", exam.getRankState() == 1 ? myExamService.getList(examId).size() : null);
		} catch (MyException e) {
			log.error("获取我的考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取我的考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 我的试卷
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	@ResponseBody
	public PageResult paper(Integer examId) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(examId)) {
				throw new MyException("参数错误：examId");
			}
			
			MyExam _myExam = myExamService.getMyExam(examId, getCurUser().getId());
			if (_myExam == null) {
				throw new MyException("无查阅权限");
			}
			Exam exam = examService.getEntity(examId);
			if (exam.getStartTime().getTime() > System.currentTimeMillis()) {
				throw new MyException("考试未开始");
			}
			
			// 组装试卷
			List<Map<String, Object>> paper = new ArrayList<>();
			List<MyQuestion> myQuestionList = myQuestionService.getList(examId, getCurUser().getId());
			
			boolean showAnswer = answerShow(exam, _myExam);// 显示标准答案
			for (MyQuestion _myQuestion : myQuestionList) {
				Map<String, Object> myQuestion = new HashMap<>();
				if (_myQuestion.getType() == 1) {
					myQuestion.put("type", _myQuestion.getType());
					myQuestion.put("chapterName", _myQuestion.getChapterName());
					myQuestion.put("chapterTxt", _myQuestion.getChapterTxt());
				} else {
					myQuestion.put("type", _myQuestion.getType());
					myQuestion.put("questionId", _myQuestion.getQuestionId());
			
					Question question = QuestionCache.getQuestion(_myQuestion.getQuestionId());// 已关联考试的试题不会改变，缓存起来加速查询。
					myQuestion.put("questionType", question.getType());
					myQuestion.put("markType", question.getMarkType());
					myQuestion.put("title", question.getTitle());
					myQuestion.put("markOptions", _myQuestion.getMarkOptions());
					myQuestion.put("score", _myQuestion.getScore());
					myQuestion.put("analysis", question.getAnalysis());
					myQuestion.put("userScore", scoreShow(exam, _myExam) ?_myQuestion.getUserScore() : null);
					{// 选项
						List<String> options = new ArrayList<>();
						if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {// 如果是单选或多选，添加选项字段
							List<QuestionOption> questionOptionList = QuestionCache.getOption(_myQuestion.getQuestionId());
							if (ValidateUtil.isValid(_myQuestion.getOptionsNo())) {// 选项乱序
								for (int optionsNo : _myQuestion.getOptionsNo()) {// 4,1,2,3
									QuestionOption questionOption = questionOptionList.get(optionsNo - 1);// 先放第4个
									options.add(questionOption.getOptions());
								}
							} else {
								for (QuestionOption questionOption : questionOptionList) {// 常规顺序
									options.add(questionOption.getOptions());
								}
							}
							
						}
						myQuestion.put("options", options);
					}
			
					{// 用户答案
						List<String> userAnswerList = new ArrayList<>();
						if (ValidateUtil.isValid(_myQuestion.getUserAnswer())) {
							if (QuestionUtil.hasTrueFalse(question) || QuestionUtil.hasQA(question)) {// 判断、问答
								userAnswerList.add(_myQuestion.getUserAnswer());
							} else if (QuestionUtil.hasSingleChoice(question)) {// 单选
								if (ValidateUtil.isValid(_myQuestion.getOptionsNo())) {// 选项乱序：4,1,3,2 ，填写答案：B，回显：D
									userAnswerList.add(_myQuestion.getOptionsNoCache().get(_myQuestion.getUserAnswer()));
								} else {
									userAnswerList.add(_myQuestion.getUserAnswer());
								}
							}  else if (QuestionUtil.hasMultipleChoice(question)) {//多选
								String[] userAnswers = _myQuestion.getUserAnswer().split(",");
								if (ValidateUtil.isValid(_myQuestion.getOptionsNo())) {
									for (String userAnswer : userAnswers) {
										userAnswerList.add(_myQuestion.getOptionsNoCache().get(userAnswer));
									}
								} else {
									Collections.addAll(userAnswerList, userAnswers);
								}
							} else if (QuestionUtil.hasFillBlank(question)) {// 填空
								Collections.addAll(userAnswerList, _myQuestion.getUserAnswer().split("\n", -1));
							}
						}
						myQuestion.put("userAnswers", userAnswerList);
					}
					
					{// 标准答案
						List<String> answerList = new ArrayList<>();
						if (showAnswer) {
							List<QuestionAnswer> questionAnswerList = QuestionCache.getAnswer(_myQuestion.getQuestionId());
							for(QuestionAnswer answer : questionAnswerList){
								if (QuestionUtil.hasTrueFalse(question) 
										|| (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {
									answerList.add(answer.getAnswer());
								} else if (QuestionUtil.hasSingleChoice(question)) {
									if (ValidateUtil.isValid(_myQuestion.getOptionsNo())) {
										answerList.add(_myQuestion.getOptionsNoCache().get(answer.getAnswer()));
									} else {
										answerList.add(answer.getAnswer());
									}
								} else if (QuestionUtil.hasMultipleChoice(question)) {
									String[] answers = answer.getAnswer().split(",");
									if (ValidateUtil.isValid(_myQuestion.getOptionsNo())) {
										for (String _answer : answers) {
											answerList.add(_myQuestion.getOptionsNoCache().get(_answer));
										}
									} else {
										Collections.addAll(answerList, answers);
									}
								} else if (QuestionUtil.hasFillBlank(question) || (QuestionUtil.hasQA(question) 
										&& QuestionUtil.hasObjective(question))) {
									answerList.add(answer.getAnswer());
								}
							}
						}
						myQuestion.put("answers", answerList);
					}
				}
				paper.add(myQuestion);
			}
			
			return PageResultEx.ok().data(paper);
		} catch (MyException e) {
			log.error("我的试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("我的试卷错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 答题
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @param questionId
	 * @param answers
	 * @return PageResult
	 */
	@RequestMapping("/answer")
	@ResponseBody
	public PageResult answer(Integer examId, Integer questionId, String[] answers) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myExamService.answerUpdate(examId, getCurUser().getId(), questionId, answers);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("答题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("答题错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 用户交卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	@ResponseBody
	public PageResult finish(Integer examId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myExamService.finish(examId, getCurUser().getId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("用户交卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("用户交卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 标准答案显示
	 * 
	 * v1.0 zhanghc 2023年2月20日上午11:32:34
	 * @param exam
	 * @param myExam 
	 * @return boolean
	 */
	private boolean answerShow(Exam exam, MyExam myExam) {
		if (exam.getScoreState() == 1) {// 如果是考试结束后公布答案
			if (exam.getMarkType() == 1 && exam.getEndTime().getTime() < System.currentTimeMillis()) { // 如果是客观题试卷，考试结束后显示标准答案； 10:00-12:00 < 12:05
				return true;
			} 
			if (exam.getMarkType() == 2 && exam.getMarkEndTime().getTime() < System.currentTimeMillis()) {// 如果是主观题试卷，阅卷结束后显示标准答案
				return true;
			}
		} 
		
		if (exam.getScoreState() == 3) {// 如果是交卷后公布答案
			if (myExam.getState() == 3) {// 如果用户已交卷，显示标准答案
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 分数显示
	 * 
	 * v1.0 zhanghc 2023年3月2日上午11:07:27
	 * @param exam
	 * @param myExam
	 * @return boolean
	 */
	private boolean scoreShow(Exam exam, MyExam myExam) {
		return (exam.getScoreState() == 1 && exam.getMarkState() == 3) // 如果是考试结束后显示成绩，需要等到考试结束
				|| (exam.getScoreState() == 3 && myExam.getMarkState() == 3);// 如果是交卷后显示成绩，需要等到该试卷阅卷完成。比如主观题没阅卷，得不到总分，得不到是否及格
	}
}