package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionAnswer;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionAnswerService;
import com.wcpdoc.exam.core.service.QuestionOptionService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
import com.wcpdoc.exam.core.util.QuestionUtil;

/**
 * 试题控制层
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Controller
@RequestMapping("/api/question")
public class ApiQuestionController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionController.class);

	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private QuestionOptionService questionOptionService;
	@Resource
	private QuestionAnswerService questionAnswerService;
	
	/**
	 * 试题列表 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			PageOut pageout = questionService.getListpage(pageIn);
			List<Map<String, Object>> resultList = pageout.getList();
			for (Map<String, Object> result : resultList) {
				Integer id = (Integer) result.get("id");
				Integer type = (Integer) result.get("type");
				Integer markType = (Integer) result.get("markType");
				
				if (result.get("markOptions") != null) {// 前后端分离下，接口定义只有数组形式
					String[] _markOptions = result.get("markOptions").toString().split(",");
					Integer[] markOptions = new Integer[_markOptions.length]; 
					for (int i = 0; i < _markOptions.length; i++) {
						markOptions[i] = Integer.parseInt(_markOptions[i]);
					}
					result.put("markOptions", markOptions);
				} else {
					result.put("markOptions", new Integer[0]);
				}
				
				if (type == 1 || type == 2) {// 如果是单选或多选，添加选项字段
					List<QuestionOption> questionOptionList = questionOptionService.getList(id);
					List<String> options = new ArrayList<>();
					for (QuestionOption questionOption : questionOptionList) {
						options.add(questionOption.getOptions());
					}
					result.put("options", options);
				}
				
				List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(id);
				List<String> answers = new ArrayList<>();
				List<BigDecimal> scores = new ArrayList<>();
				for(QuestionAnswer answer : questionAnswerList){
					if (type == 1 || type == 4 || (type == 5 && markType == 2)) {
						answers.add(answer.getAnswer());
					} else if (type == 2) {
						Collections.addAll(answers, answer.getAnswer().split(","));
						scores.add(answer.getScore());
					} else if (type == 3 || (type == 5 && markType == 1)) {
						answers.add(answer.getAnswer());
						scores.add(answer.getScore());
					}
				}
				result.put("answers", answers);
				result.put("scores", scores);
			}
			return PageResultEx.ok().data(pageout);
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题添加
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param question
	 * @param markOptions 阅卷选项
	 * @param options 选项（单选多选时有效）
	 * @param answers 答案
	 * @param scores 答案分数（填空或智能问答有多项）
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Question question, String[] options, String[] answers, BigDecimal[] scores) {
		try {
			questionService.addEx(question, options, answers, scores);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("试题添加错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题修改
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param question
	 * @param answers
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Question question, String[] options, String[] answers, BigDecimal[] scores) {
		try {
			questionService.updateEx(question, options, answers, scores);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("试题修改错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题删除
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param ids
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer[] ids) {
		try {
			questionService.delEx(ids);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("试题删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题获取
	 * 
	 * v1.0 zhanghc 2021年7月1日下午2:18:05
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			// 校验数据有效性
			Question question = questionService.getEntity(id);
			QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
			if (questionType.getUpdateUserId().intValue() != getCurUser().getId().intValue()) {
				throw new MyException("无操作权限");
			}
			
			// 试题获取
			List<String> options = new ArrayList<>();
			if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {// 如果是单选或多选，添加选项字段
				List<QuestionOption> questionOptionList = questionOptionService.getList(id);
				for (QuestionOption questionOption : questionOptionList) {
					options.add(questionOption.getOptions());
				}
			}
			
			List<QuestionAnswer> questionAnswerList = questionAnswerService.getList(question.getId());
			List<Object> answers = new ArrayList<>();
			List<BigDecimal> scores = new ArrayList<>();
			for(QuestionAnswer answer : questionAnswerList){
				if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasTrueFalse(question) || (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {
					answers.add(answer.getAnswer());
				} else if (QuestionUtil.hasMultipleChoice(question)) {
					Collections.addAll(answers, answer.getAnswer().split(","));
					scores.add(answer.getScore());
				} else if (QuestionUtil.hasFillBlank(question) || (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {
					answers.add(answer.getAnswer().split("\n"));
					scores.add(answer.getScore());
				}
			}
			
			PageResultEx pageResult = PageResultEx.ok()
					.addAttr("id", question.getId())
					.addAttr("type", question.getType())
					.addAttr("title", question.getTitle())
					.addAttr("options", options)
					.addAttr("markType", question.getMarkType())
					.addAttr("analysis", question.getAnalysis())
					.addAttr("questionTypeId", question.getQuestionTypeId())
					.addAttr("score", question.getScore())
					.addAttr("markOptions", question.getMarkOptions())
					.addAttr("answers", answers)
					.addAttr("scores", scores)
					.addAttr("state", question.getState());
			return pageResult;
		} catch (MyException e) {
			log.error("试题获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("试题获取错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题复制
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/copy")
	@ResponseBody
	public PageResult copy(Integer id) {
		try {
			questionService.copy(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("试题复制错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("试题复制错误：", e);
			return PageResult.err();
		}
	}
}