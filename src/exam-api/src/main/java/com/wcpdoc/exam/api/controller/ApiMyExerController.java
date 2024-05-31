package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.entity.ExerRmk;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.service.ExamCacheService;
import com.wcpdoc.exam.core.service.ExerRmkService;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.QuestionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 我的练习控制层
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@RestController
@RequestMapping("/api/myExer")
@Slf4j
public class ApiMyExerController extends BaseController {
	
	@Resource
	private ExerService exerService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private ExerRmkService exerRmkService;
	@Resource
	private ExamCacheService examCacheService;
	
	/**
	 * 我的练习列表
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	public PageResult listpage(PageIn pageIn) {
		try {
			pageIn.addParm("examUserId", getCurUser().getId());
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
	 * 练习获取
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/get")
	public PageResult get(Integer exerId) {
		try {
			Exer exer = exerService.getById(exerId);
			if (!exer.getUserIds().contains(getCurUser().getId())) {
				throw new MyException("无操作权限");
			}
			return PageResultEx.ok()
					.addAttr("id", exer.getId())
					.addAttr("name", exer.getName())
					.addAttr("questionTypeId", exer.getQuestionTypeId())
					.addAttr("questionTypeName", questionTypeService.getById(exer.getQuestionTypeId()).getName())
					.addAttr("startTime", exer.getStartTime())
					.addAttr("endTime", exer.getEndTime())
					.addAttr("rmkState", exer.getRmkState())
					;
		} catch (MyException e) {
			log.error("练习删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习删除错误：", e);
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
	public PageResult question(Integer exerId, Integer questionId) {
		try {
			// 数据校验
			Exer exer = exerService.getById(exerId);
			if (exer.getState() == 0) {
				throw new MyException("已删除");
			}
			long curTime = System.currentTimeMillis();
			if (!(exer.getStartTime().getTime() < curTime && curTime < exer.getEndTime().getTime())) {
				throw new MyException("时间已过期");
			}
			if (ValidateUtil.isValid(exer.getUserIds()) && !exer.getUserIds().contains(getCurUser().getId())) {
				throw new MyException("无权限");
			}
			
			// 试题获取
			Map<String, Object> questionMap = new HashMap<>();
			Question question = examCacheService.getQuestion(questionId);
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
					List<QuestionOption> questionOptionList = examCacheService.getQuestionOptionList(question.getId());
					for (QuestionOption questionOption : questionOptionList) {
						options.add(questionOption.getOptions());
					}
				}
				questionMap.put("options", options);
			}
			{// 试题答案
				List<QuestionAnswer> questionAnswerList = examCacheService.getQuestionAnswerList(question.getId());
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
	public PageResult questionList(Integer exerId) {
		try {
			// 数据有效性校验
			Exer exer = exerService.getById(exerId);
			if (exer.getState() == 0) {
				throw new MyException("练习已删除");
			}
			long curTime = System.currentTimeMillis();
			if (!(exer.getStartTime().getTime() < curTime && curTime < exer.getEndTime().getTime())) {
				throw new MyException("时间已过期");
			}
			if (!exer.getUserIds().contains(getCurUser().getId())) {
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
	
	/**
	 * 评论列表
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/rmkListpage")
	public PageResult rmkListpage(PageIn pageIn) {
		try {
			PageOut pageOut = exerRmkService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("likeUserIds") != null 
						&& map.get("likeUserIds").toString().contains(String.format(",%s,", getCurUser().getId()))) {
					map.put("hasLike", true);
				} else {
					map.put("hasLike", false);
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("评论列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 评论
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * @param exerRmk 评论
	 * @param anon 是否匿名（true：是；false：否）
	 * @return PageResult
	 */
	@RequestMapping("/rmk")
	public PageResult rmk(ExerRmk exerRmk, Boolean anon) {
		try {
			exerRmkService.rmk(exerRmk, anon);
			return PageResultEx.ok().addAttr("id", exerRmk.getId());
		} catch (MyException e) {
			log.error("评论错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("评论错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 评论点赞
	 * 
	 * v1.0 zhanghc 2023年4月17日下午7:52:03
	 * @param exerRmkId 评论ID
	 * @return PageResult
	 */
	@RequestMapping("/rmkLike")
	public PageResult rmkLike(Integer exerRmkId) {
		try {
			exerRmkService.like(exerRmkId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("评论点赞错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("评论点赞错误：", e);
			return PageResult.err();
		}
	}
}
