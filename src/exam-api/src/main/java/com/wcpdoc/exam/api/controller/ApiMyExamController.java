package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyQuestion;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.QuestionUtil;
import com.wcpdoc.notify.service.NotifyService;

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
	private MyMarkService myMarkService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private UserService userService;
	@Resource
	private NotifyService notifyService;
	@Resource
	private ParmService parmService;
	@Resource
	private ExamService examService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	
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
				map.put("curTime", DateUtil.formatDateTime(new Date()));
				if ((Integer)map.remove("examScoreState") == 2) {
					map.put("totalScore", null);// 不显示分数
					map.put("answerState", null);// 不显示及格状态
				}
				if ((Integer)map.remove("examRankState") == 2) {
					map.put("no", null);// 不显示排名
					map.put("userNum", null);// 不显示排名
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
			return PageResultEx.ok()
					.addAttr("answerStartTime", myExam.getAnswerStartTime())
					.addAttr("answerEndTime", myExam.getAnswerEndTime())
					.addAttr("markStartTime", myExam.getMarkStartTime())
					.addAttr("markEndTime", myExam.getMarkEndTime())
					.addAttr("objectiveScore", myExam.getObjectiveScore())
					.addAttr("totalScore", myExam.getTotalScore())
					.addAttr("state", myExam.getState())
					.addAttr("markState", myExam.getMarkState())
					.addAttr("answerState", myExam.getAnswerState());
		} catch (MyException e) {
			log.error("获取我的错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取我的考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试卷信息
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
			
			// 组装试卷数据
			List<MyQuestion> myQuestionList = myQuestionService.getList(examId, getCurUser().getId());
			List<Map<String, Object>> paper = new ArrayList<>();
			for (MyQuestion _myQuestion : myQuestionList) {
				Map<String, Object> myQuestion = new HashMap<>();
				if (_myQuestion.getType() == 1) {
					myQuestion.put("type", _myQuestion.getType());
					myQuestion.put("chapterName", _myQuestion.getChapterName());
					myQuestion.put("chapterTxt", _myQuestion.getChapterTxt());
				} else {
					myQuestion.put("type", _myQuestion.getType());
					Question _question = questionService.getEntity(_myQuestion.getQuestionId());
					List<String> _options = new ArrayList<>();
					if (QuestionUtil.hasSingleChoice(_question) || QuestionUtil.hasMultipleChoice(_question)) {// 如果是单选或多选，添加选项字段
						List<QuestionOption> questionOptionList = questionOptionService.getList(_question.getId());
						for (QuestionOption questionOption : questionOptionList) {
							_options.add(questionOption.getOptions());
						}
					}
					List<Object> _answers = new ArrayList<>();
					List<BigDecimal> _answerScores = new ArrayList<>();
					if (QuestionUtil.hasSingleChoice(_question) || QuestionUtil.hasTrueFalse(_question) 
							|| (QuestionUtil.hasQA(_question) && QuestionUtil.hasSubjective(_question))) {// 单选、判断、主观问答
						if (ValidateUtil.isValid(_myQuestion.getUserAnswer())) {
							_answers.add(_myQuestion.getUserAnswer());
						}
					} else if (QuestionUtil.hasMultipleChoice(_question)) {//多选
						if (ValidateUtil.isValid(_myQuestion.getUserAnswer())) {
							Collections.addAll(_answers, _myQuestion.getUserAnswer().split(","));
						}
						_answerScores.add(_myQuestion.getScores()[0]);// 漏选分值
					} else if (QuestionUtil.hasFillBlank(_question) || (QuestionUtil.hasQA(_question) 
							&& QuestionUtil.hasObjective(_question))) {// 填空或客观问答
						if (ValidateUtil.isValid(_myQuestion.getUserAnswer())) {
							Collections.addAll(_answers, _myQuestion.getUserAnswer().split("\n", -1));
						}
						Collections.addAll(_answerScores, _myQuestion.getScores());
					}
					
					Map<String, Object> question = new HashMap<>();
					question.put("id", _question.getId());
					question.put("type", _question.getType());
					question.put("title", _question.getTitle());
					question.put("options", _options);
					question.put("markType", _question.getMarkType());
					question.put("analysis", _question.getAnalysis());
					question.put("questionTypeId", _question.getQuestionTypeId());
					question.put("score", _myQuestion.getScore());
					question.put("markOptions", _myQuestion.getMarkOptions());
					question.put("answers", _answers);
					question.put("answerScores", _myQuestion.getScores());
					question.put("state", _question.getState());
					myQuestion.put("question", question);
				}
				
				paper.add(myQuestion);
			}
			
			return PageResultEx.ok().data(paper);
		} catch (MyException e) {
			log.error("试卷信息错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试卷信息错误：", e);
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
			log.error("更新答案错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("更新答案错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 交卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	@ResponseBody
	public PageResult finish(Integer examId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 5000)) {
				throw new MyException("尝试加读锁失败");
			}
			myExamService.finish(examId, getCurUser().getId());
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成交卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成交卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
}