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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.service.OnlineUserService;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.cache.AutoMarkCache;
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamQuestion;
import com.wcpdoc.exam.core.entity.ExamRule;
import com.wcpdoc.exam.core.entity.MyExam;
import com.wcpdoc.exam.core.entity.MyMark;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.ex.ExamInfo;
import com.wcpdoc.exam.core.service.ExamQuestionService;
import com.wcpdoc.exam.core.service.ExamRuleService;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.core.service.MyExamService;
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.util.QuestionUtil;

/**
 * 考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/exam")
public class ApiExamController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiExamController.class);

	@Resource
	private ExamService examService;
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private UserService userService;
	@Resource
	private OnlineUserService onlineUserService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private ExamQuestionService examQuestionService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	@Resource
	private ExamRuleService examRuleService;

	/**
	 * 考试列表
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = examService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("sxes") == null) {
					map.put("sxes", new Integer[0]);
				} else {
					map.put("sxes", ((String)map.get("sxes")).split(","));
				}
			}
			
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("考试列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 发布
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param examInfo
	 * @return PageResult
	 */
	@RequestMapping("/publish")
	@ResponseBody
	public PageResult publish(@RequestBody ExamInfo examInfo) {
		try {
			examService.publish(examInfo);
			return PageResultEx.ok();
		} catch (MyException e) {e.printStackTrace();
			log.error("完成添加考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取考试信息
	 * 
	 * v1.0 zhanghc 2022年10月25日下午3:30:55
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	@ResponseBody
	public PageResult paper(Integer id) {
		Exam exam = examService.getEntity(id);
		List<Map<String, Object>> examQuestions = new ArrayList<>();
		List<Map<String, Object>> examRules = new ArrayList<>();
		if (exam.getGenType() == 1) {// 人工组卷
			List<ExamQuestion> _examQuestionList = examQuestionService.getList(id);
			for (ExamQuestion _examQuestion : _examQuestionList) {
				Map<String, Object> examQuestion = new HashMap<>();
				examQuestion.put("type", _examQuestion.getType());
				if (_examQuestion.getType() == 1) {
					examQuestion.put("chapterName", _examQuestion.getChapterName());
					examQuestion.put("chapterTxt", _examQuestion.getChapterTxt());
				} else {
					Question _question = questionService.getEntity(_examQuestion.getQuestionId());
					examQuestion.put("questionId", _question.getId());
					examQuestion.put("questionType", _question.getType());
					examQuestion.put("markType", _question.getMarkType());
					examQuestion.put("title", _question.getTitle());
					examQuestion.put("markOptions", _examQuestion.getMarkOptions());
					examQuestion.put("score", _examQuestion.getScore());
					examQuestion.put("scores", _examQuestion.getScores());
					examQuestion.put("analysis", _question.getAnalysis());
					
					List<String> _options = new ArrayList<>();
					if (QuestionUtil.hasSingleChoice(_question) || QuestionUtil.hasMultipleChoice(_question)) {// 如果是单选或多选，添加选项字段
						List<QuestionOption> questionOptionList = questionOptionService.getList(_question.getId());
						for (QuestionOption questionOption : questionOptionList) {
							_options.add(questionOption.getOptions());
						}
						examQuestion.put("options", _options);
					}
					
					List<QuestionAnswer> _questionAnswerList = questionAnswerService.getList(_question.getId());
					List<Object> _answers = new ArrayList<>();
					for(QuestionAnswer answer : _questionAnswerList){
						if (QuestionUtil.hasSingleChoice(_question) || QuestionUtil.hasTrueFalse(_question) 
								|| (QuestionUtil.hasQA(_question) && QuestionUtil.hasSubjective(_question))) {
							_answers.add(answer.getAnswer());
						} else if (QuestionUtil.hasMultipleChoice(_question)) {
							Collections.addAll(_answers, answer.getAnswer().split(","));
						} else if (QuestionUtil.hasFillBlank(_question) || (QuestionUtil.hasQA(_question) 
								&& QuestionUtil.hasObjective(_question))) {
							_answers.add(answer.getAnswer());
						}
					}
					examQuestion.put("answers", _answers);
				}
				examQuestions.add(examQuestion);
			}
		} else if (exam.getGenType() == 2) {// 随机组卷
			List<ExamRule> _examRuleList = examRuleService.getList(id);
			for (ExamRule _examRule : _examRuleList) {
				Map<String, Object> examRule = new HashMap<>();
				examRule.put("type", _examRule.getType());
				if (_examRule.getType() == 1) {
					examRule.put("chapterName", _examRule.getChapterName());
					examRule.put("chapterTxt", _examRule.getChapterTxt());
				} else {
					examRule.put("questionTypeId", _examRule.getQuestionTypeId());
					examRule.put("questionType", _examRule.getQuestionType());
					examRule.put("markType", _examRule.getMarkType());
					examRule.put("markOptions", _examRule.getMarkOptions());
					examRule.put("num", _examRule.getNum());
					examRule.put("score", _examRule.getScore());
					examRule.put("scores", _examRule.getQuestionType() == 2 ? _examRule.getScores() : new BigDecimal[0]);// 客观填空等不需要页面处理
				}
				examRules.add(examRule);
			}
		}
		
		List<MyExam> _myExamList = myExamService.getList(id);
		List<Integer> examUserIdList = new ArrayList<>();
		for (MyExam myExam : _myExamList) {
			examUserIdList.add(myExam.getUserId());
		}
		
		List<Integer> markUserIdList = new ArrayList<>();
		List<MyMark> myMarkList = myMarkService.getList(id);
		for (MyMark myMark : myMarkList) {
			markUserIdList.add(myMark.getMarkUserId());
		}
		
		return PageResultEx.ok()
				.addAttr("id", exam.getId())
				.addAttr("name", exam.getName())
				.addAttr("paperName", exam.getPaperName())
				.addAttr("startTime", DateUtil.formatDateTime(exam.getStartTime()))
				.addAttr("endTime", DateUtil.formatDateTime(exam.getEndTime()))
				.addAttr("markStartTime", ValidateUtil.isValid(exam.getMarkStartTime()) ? DateUtil.formatDateTime(exam.getMarkStartTime()) : null)
				.addAttr("markEndTime", ValidateUtil.isValid(exam.getMarkEndTime()) ? DateUtil.formatDateTime(exam.getMarkEndTime()) : null)
				.addAttr("genType", exam.getGenType())
				.addAttr("passScore", exam.getPassScore())
				.addAttr("showType", exam.getShowType())
				.addAttr("anonState", exam.getAnonState())
				.addAttr("scoreState", exam.getScoreState())
				.addAttr("rankState", exam.getRankState())
				.addAttr("sxes", exam.getSxes())
				.addAttr("state", exam.getState())
				.addAttr("examQuestions", examQuestions)
				.addAttr("examRules", examRules)
				.addAttr("examUserIds", examUserIdList)
				.addAttr("markUserIds", markUserIdList);
	}
	
	/**
	 * 完成删除考试
	 * 
	 * v1.0 zhanghc 2017-06-11 09:13:23
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			examService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取考试
	 * 
	 * v1.0 zhanghc 2021年12月21日下午4:36:14
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			Exam exam = examService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", exam.getId())
					.addAttr("name", exam.getName())
					.addAttr("paperName", exam.getPaperName())
					.addAttr("startTime", ValidateUtil.isValid(exam.getStartTime()) ? DateUtil.formatDateTime(exam.getStartTime()) : null)
					.addAttr("endTime", ValidateUtil.isValid(exam.getEndTime()) ? DateUtil.formatDateTime(exam.getEndTime()) : null)
					.addAttr("markStartTime", ValidateUtil.isValid(exam.getMarkStartTime()) ? DateUtil.formatDateTime(exam.getMarkStartTime()) : null)
					.addAttr("markEndTime", ValidateUtil.isValid(exam.getMarkEndTime()) ? DateUtil.formatDateTime(exam.getMarkEndTime()) : null)
					.addAttr("markState", exam.getMarkState())
					.addAttr("scoreState", exam.getScoreState())
					.addAttr("rankState", exam.getRankState())
					.addAttr("anonState", exam.getAnonState())
					.addAttr("passScore", exam.getPassScore())
					.addAttr("totalScore", exam.getTotalScore())
					.addAttr("markType", exam.getMarkType())
					.addAttr("showType", exam.getShowType())
					.addAttr("genType", exam.getGenType())
					.addAttr("sxes", exam.getSxes())
					.addAttr("state", exam.getState())
					.addAttr("curTime", DateUtil.formatDateTime(new Date()))
					;
		} catch (MyException e) {
			log.error("获取考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取考试错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 更新考试阅卷用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * 
	 * @param id
	 * @param examUserIds
	 * @param markUserIds
	 * @return PageResult
	 */
	@RequestMapping("/userAdd")
	@ResponseBody
	public PageResult userAdd(Integer id, String[] examUserIds, Integer[] markUserIds) {
		try {
			examService.userAdd(id, examUserIds, markUserIds);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("更新考试阅卷用户错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("更新考试阅卷用户错误：", e);
			return PageResult.err();
		}
	}

//	/**
//	 * 在线用户
//	 * 
//	 * v1.0 chenyun 2021年9月7日下午1:27:31
//	 * 
//	 * @param ids
//	 * @return PageResult
//	 */
//	@RequestMapping("/onlineUser")
//	@ResponseBody
//	public PageResult onlineUser(Integer id) {
//		try {
//			Exam exam = examService.getEntity(id);
////			if (exam.getStartTime().getTime() > System.currentTimeMillis()) {
////				throw new MyException("考试未开始");
////			}
////			if (exam.getEndTime().getTime() < System.currentTimeMillis()) {
////				throw new MyException("考试已结束");
////			}
//
//			List<Map<String, Object>> examUserList = examService.getExamUserList(id);
//			for (Map<String, Object> map : examUserList) {
//				map.put("userId", map.remove("id"));
//				map.put("userName", map.remove("name"));
//
//				Integer userId = (Integer) map.get("userId");
//				OnlineUser onlineUser = onlineUserService.getEntity(userId);
//				if (onlineUser == null) {
//					map.put("online", false);
//					map.put("onlineTime", null);
//					continue;
//				}
//
//				map.put("online", onlineUser.getState());
//				map.put("onlineTime", DateUtil.formatDateTime(onlineUser.getUpdateTime()));
//			}
//
//			return PageResultEx.ok().data(new PageOut(examUserList, examUserList.size()));
//		} catch (MyException e) {
//			log.error("在线用户错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("在线用户错误：", e);
//			return PageResult.err();
//		}
//	}
	
	/**
	 * 变更考试时间
	 * 
	 * v1.0 zhanghc 2022年4月17日下午6:52:08
	 * @param id 考试ID
	 * @param timeType 时间类型：1：考试开始时间；2：考试结束时间；3：阅卷开始时间；4：阅卷结束时间
	 * @param minute 分钟数
	 * @return PageResult
	 */
	@RequestMapping("/time")
	@ResponseBody
	public PageResult time(Integer id, Integer timeType, Integer minute) {
		try {
			if (!AutoMarkCache.tryWriteLock(id, 20000)) {
				throw new MyException("尝试加写锁失败");
			}
			examService.timeUpdate(id, timeType, minute);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("变更考试时间错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("变更考试时间错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseWriteLock(id);
		}
	}
	
	/**
	 * 考试邮件通知
	 * 
	 * v1.0 chenyun 2022年3月28日下午2:23:19
	 * @param id
	 * @param content
	 * @param notifyType
	 * @return PageResult
	 */
	@RequestMapping("/mail")
	@ResponseBody
	public PageResult mail(Integer id, Integer notifyType, String content) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(id)) {
				throw new MyException("参数错误:id");
			}
			if (!ValidateUtil.isValid(content)) {
				throw new MyException("参数错误:content");
			}
			if (notifyType != 1 && notifyType != 2 && notifyType != 3) {
				throw new MyException("参数错误:state");
			}
			
			Exam exam = examService.getEntity(id);
			if (exam == null) {
				throw new MyException("参数错误:id");
			}
			if (exam.getState() != 1) {
				throw new MyException("考试未发布");
			}
			if (exam.getEndTime().before(new Date()) ) {
				throw new MyException("考试时间已结束");
			}
			examService.mail(exam, notifyType, content);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("考试邮件通知错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("考试邮件通知错误：", e);
			return PageResult.err();
		}
	}
}