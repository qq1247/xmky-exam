package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
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
import com.wcpdoc.exam.core.service.MyMarkService;
import com.wcpdoc.exam.core.service.MyQuestionService;
import com.wcpdoc.exam.core.util.QuestionUtil;

/**
 * 我的阅卷控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/api/myMark")
public class ApiMyMarkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiMyMarkController.class);
	
	@Resource
	private MyMarkService myMarkService;
	@Resource
	private MyExamService myExamService;
	@Resource
	private MyQuestionService myQuestionService;
	@Resource
	private ExamService examService;
	
	/**
	 * 我的阅卷列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			PageOut pageOut = myMarkService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 我的阅卷用户列表
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 */
	@RequestMapping("/userListpage")
	@ResponseBody
	public PageResult userListpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = myMarkService.getUserListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的阅卷用户列表：", e);
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
	public PageResult get(Integer examId, Integer userId) {
		try {
			MyExam myExam = myExamService.getMyExam(examId, userId);
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
			log.error("获取我的考试错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取我的考试错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取用户试卷
	 * 
	 * v1.0 zhanghc 2022年5月18日下午1:21:07
	 * @param examId
	 * @param userId
	 * @return PageResult
	 */
	@RequestMapping("/paper")
	@ResponseBody
	public PageResult paper(Integer examId, Integer userId) {
		try {
			// 校验数据有效性
			if (!ValidateUtil.isValid(examId)) {
				throw new MyException("参数错误：examId");
			}
			if (!ValidateUtil.isValid(userId)) {
				throw new MyException("参数错误：userId");
			}
			
			MyExam _myExam = myExamService.getMyExam(examId, userId);
			if (_myExam == null) {
				throw new MyException("无查阅权限");
			}
			if (_myExam.getMarkUserId().intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无查阅权限");
			}
			Exam exam = examService.getEntity(examId);
			if (exam.getMarkType() == 2) {
				if (exam.getMarkStartTime().getTime() > System.currentTimeMillis()) {
					throw new MyException("阅卷未开始");
				}
			}
			
			// 组装试卷
			List<Map<String, Object>> paper = new ArrayList<>();
			List<MyQuestion> myQuestionList = myQuestionService.getList(examId, userId);
			
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
					myQuestion.put("userScore", _myQuestion.getUserScore());
					{// 选项
						List<String> options = new ArrayList<>();
						if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {// 如果是单选或多选，添加选项字段
							List<QuestionOption> questionOptionList = QuestionCache.getOption(_myQuestion.getQuestionId());
							for (QuestionOption questionOption : questionOptionList) {
								options.add(questionOption.getOptions());
							}
						}
						myQuestion.put("options", options);
					}
			
					{// 用户答案
						List<String> userAnswerList = new ArrayList<>();
						if (ValidateUtil.isValid(_myQuestion.getUserAnswer())) {
							if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasTrueFalse(question) 
									|| (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {// 单选、判断、问答
								userAnswerList.add(_myQuestion.getUserAnswer());
							} else if (QuestionUtil.hasMultipleChoice(question)) {//多选
								Collections.addAll(userAnswerList, _myQuestion.getUserAnswer().split(","));
							} else if (QuestionUtil.hasFillBlank(question)) {// 填空
								Collections.addAll(userAnswerList, _myQuestion.getUserAnswer().split("\n", -1));
							}
						}
						myQuestion.put("userAnswers", userAnswerList);
					}
					
					{// 标准答案（前面校验是是否阅卷已开始，可以显示答案）
						List<String> answerList = new ArrayList<>();
						List<QuestionAnswer> questionAnswerList = QuestionCache.getAnswer(_myQuestion.getQuestionId());
						for(QuestionAnswer answer : questionAnswerList){
							if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasTrueFalse(question) 
									|| (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {
								answerList.add(answer.getAnswer());
							} else if (QuestionUtil.hasMultipleChoice(question)) {
								Collections.addAll(answerList, answer.getAnswer().split(","));
							} else if (QuestionUtil.hasFillBlank(question) || (QuestionUtil.hasQA(question) 
									&& QuestionUtil.hasObjective(question))) {
								answerList.add(answer.getAnswer());
							}
						}
						myQuestion.put("answers", answerList);
					}
				}
				paper.add(myQuestion);
			}
			
			return PageResultEx.ok().data(paper);
		} catch (MyException e) {
			log.error("获取用户试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取用户试卷错误：", e);
			return PageResult.err(); 
		}
	}
	
//	/**
//	 * 分配试卷
//	 * 
//	 * v1.0 zhanghc 2023年2月23日下午2:31:16
//	 * @param examId 考试ID
//	 * @param num 分配份数
//	 * @return PageResult
//	 */
//	@RequestMapping("/assign")
//	@ResponseBody
//	public PageResult assign(Integer examId, Integer num) {
//		try {
//			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
//				throw new MyException("尝试加读锁失败");
//			}
//			myMarkService.assign(examId, num);
//			return PageResultEx.ok();
//		} catch (MyException e) {
//			log.error("分配试卷错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("分配试卷错误：", e);
//			return PageResult.err();
//		} finally {
//			AutoMarkCache.releaseReadLock(examId);
//		}
//	}
	
	/**
	 * 打分
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId 考试ID
	 * @param userId 考试用户ID
	 * @param questionId 试题ID
	 * @param userScore 用户得分
	 * @return PageResult
	 */
	@RequestMapping("/score")
	@ResponseBody
	public PageResult score(Integer examId, Integer userId, Integer questionId, BigDecimal userScore) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myMarkService.scoreUpdate(examId, userId, questionId, userScore);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("打分错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("打分错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
	
	/**
	 * 阅卷
	 * 
	 * v1.0 zhanghc 2017年6月26日下午12:30:20
	 * @param examId
	 * @return PageResult
	 */
	@RequestMapping("/finish")
	@ResponseBody
	public PageResult finish(Integer examId, Integer userId) {
		try {
			if (!AutoMarkCache.tryReadLock(examId, 2000)) {
				throw new MyException("尝试加读锁失败");
			}
			myMarkService.finish(examId, userId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("阅卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("阅卷错误：", e);
			return PageResult.err();
		} finally {
			AutoMarkCache.releaseReadLock(examId);
		}
	}
}