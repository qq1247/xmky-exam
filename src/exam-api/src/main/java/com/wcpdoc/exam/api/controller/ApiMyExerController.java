package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.wcpdoc.exam.core.cache.QuestionCache;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.QuestionUtil;

/**
 * 我的练习控制层
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Controller
@RequestMapping("/api/myExer")
public class ApiMyExerController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiMyExerController.class);
	
	@Resource
	private ExerService exerService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	
	/**
	 * 我的练习列表
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = exerService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				map.remove("userIds");// 我的练习不需要该字段
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("我的练习列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 模拟练习获取
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer exerId) {
		try {
			Exer exer = exerService.getEntity(exerId);
			return PageResultEx.ok()
					.addAttr("id", exer.getId())
					.addAttr("name", exer.getName())
					.addAttr("questionTypeId", exer.getQuestionTypeId())
					.addAttr("questionTypeName", questionTypeService.getEntity(exer.getQuestionTypeId()).getName())
					.addAttr("startTime", DateUtil.formatDateTime(exer.getStartTime()))
					.addAttr("endTime", DateUtil.formatDateTime(exer.getEndTime()))
					.addAttr("userIds", exer.getUserIds())
					.addAttr("rmkState", exer.getRmkState())
					;
		} catch (MyException e) {
			log.error("模拟练习删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("模拟练习删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 我的练习试题
	 * 
	 * v1.0 zhanghc 2021年7月1日下午2:18:05
	 * @param exerId
	 * @param questionId
	 * @return PageResult
	 */
	@RequestMapping("/question")
	@ResponseBody
	public PageResult question(Integer exerId, Integer questionId) {
		try {
			// 数据有效性校验
			Exer exer = exerService.getEntity(exerId);
			if (exer.getState() == 0) {
				throw new MyException("无权限");
			}
			long curTime = System.currentTimeMillis();
			if (!(exer.getStartTime().getTime() < curTime && curTime < exer.getEndTime().getTime())) {
				throw new MyException("时间已过期");
			}
			Set<Integer> userIdSet = new HashSet<>();
			userIdSet.addAll(Arrays.asList(exer.getUserIds()));
			if (ValidateUtil.isValid(userIdSet) && !userIdSet.contains(getCurUser().getId())) {
				throw new MyException("无权限");
			}
			
			// 试题获取
			Map<String, Object> questionMap = new HashMap<>();
			Question question = QuestionCache.getQuestion(questionId);
			questionMap.put("id", question.getId());
			questionMap.put("type", question.getType());
			questionMap.put("markType", question.getMarkType());
			questionMap.put("title", question.getTitle());
			questionMap.put("markOptions", question.getMarkOptions());
			questionMap.put("score", question.getScore());
			questionMap.put("analysis", question.getAnalysis());
			{// 试题选项
				List<String> options = new ArrayList<>();
				if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {
					List<QuestionOption> questionOptionList = QuestionCache.getOption(question.getId());
					for (QuestionOption questionOption : questionOptionList) {
						options.add(questionOption.getOptions());
					}
				}
				questionMap.put("options", options);
			}
			{// 试题答案
				List<QuestionAnswer> questionAnswerList = QuestionCache.getAnswer(question.getId());
				List<Object> answers = new ArrayList<>();
				List<BigDecimal> scores = new ArrayList<>();
				for(QuestionAnswer answer : questionAnswerList) {
					if (QuestionUtil.hasSingleChoice(question) 
							|| QuestionUtil.hasTrueFalse(question) 
							|| (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {
						answers.add(answer.getAnswer());
					} else if (QuestionUtil.hasMultipleChoice(question)) {
						Collections.addAll(answers, answer.getAnswer().split(","));
						scores.add(answer.getScore());
					} else if (QuestionUtil.hasFillBlank(question) 
							|| (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {
						answers.add(answer.getAnswer());
						scores.add(answer.getScore());
					}
				}
				questionMap.put("answers", answers);
				questionMap.put("scores", scores);
			}
			return PageResultEx.ok().data(questionMap);
		} catch (MyException e) {
			log.error("我的练习试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("我的练习试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 我的练习试题列表
	 * 
	 * v1.0 zhanghc 2022年3月18日上午10:32:57
	 * @param exerId
	 * @return PageResult
	 */
	@RequestMapping("/questionList")
	@ResponseBody
	public PageResult questionList(Integer exerId) {
		try {
			// 数据有效性校验
			Exer exer = exerService.getEntity(exerId);
			if (exer.getState() == 0) {
				throw new MyException("无权限");
			}
			long curTime = System.currentTimeMillis();
			if (!(exer.getStartTime().getTime() < curTime && curTime < exer.getEndTime().getTime())) {
				throw new MyException("时间已过期");
			}
			Set<Object> userIdSet = new HashSet<>();
			userIdSet.addAll(Arrays.asList(exer.getUserIds()));
			if (ValidateUtil.isValid(userIdSet) && !userIdSet.contains(getCurUser().getId())) {
				throw new MyException("无权限");
			}
			
			// 试题列表
			List<Integer> questionIds = questionService.getIds(exer.getQuestionTypeId());
			return PageResultEx.ok().data(questionIds);
		} catch (MyException e) {
			log.error("我的练习试题列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("我的练习试题列表错误：", e);
			return PageResult.err();
		}
	}
}
