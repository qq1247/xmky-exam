package com.wcpdoc.exam.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
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
 * 模拟练习控制层
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Controller
@RequestMapping("/api/exer")
public class ApiExerController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiExerController.class);
	
	@Resource
	private ExerService exerService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	
	/**
	 * 模拟练习列表
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			if (pageIn.get("list") != null && pageIn.get("list").equals("1")) {
				pageIn.addAttr("curUserId", getCurUser().getId());
			}else{
				pageIn.addAttr("readUserIds", getCurUser().getId());
			}
			return PageResultEx.ok().data(exerService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("模拟练习列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 模拟练习添加
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Exer exer) {
		try {
			exerService.addEx(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("模拟练习添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("模拟练习添加错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 模拟练习删除
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			Exer exer = exerService.getEntity(id);
			exer.setState(2);
			exer.setUpdateTime(new Date());
			exer.setUpdateUserId(getCurUser().getId());
			exerService.update(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("模拟练习删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("模拟练习删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 模拟练习试题
	 * 
	 * v1.0 zhanghc 2021年7月1日下午2:18:05
	 * @param id
	 * @param questionId
	 * @return PageResult
	 */
	@RequestMapping("/question")
	@ResponseBody
	public PageResult question(Integer id, Integer questionId) {
		try {
			// 数据有效性校验
			Exer exer = exerService.getEntity(id);
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
			
			// 试题获取
			Map<String, Object> questionMap = new HashMap<>();
			Question question = QuestionCache.getQuestion(questionId);// 已关联考试的试题不会改变，缓存起来加速查询。
			questionMap.put("id", question.getId());
			questionMap.put("type", question.getType());
			questionMap.put("markType", question.getMarkType());
			questionMap.put("title", question.getTitle());
			questionMap.put("markOptions", question.getMarkOptions());
			questionMap.put("score", question.getScore());
			questionMap.put("analysis", question.getAnalysis());
			{
				List<String> options = new ArrayList<>();
				if (QuestionUtil.hasSingleChoice(question) || QuestionUtil.hasMultipleChoice(question)) {// 如果是单选或多选，添加选项字段
					List<QuestionOption> questionOptionList = QuestionCache.getOption(question.getId());
					for (QuestionOption questionOption : questionOptionList) {
						options.add(questionOption.getOptions());
					}
				}
				questionMap.put("options", options);
			}
			{
				List<String> answerList = new ArrayList<>();
				List<QuestionAnswer> questionAnswerList = QuestionCache.getAnswer(question.getId());
				for(QuestionAnswer answer : questionAnswerList){
					if (QuestionUtil.hasTrueFalse(question) 
							|| (QuestionUtil.hasQA(question) && QuestionUtil.hasSubjective(question))) {
						answerList.add(answer.getAnswer());
					} else if (QuestionUtil.hasSingleChoice(question)) {
						answerList.add(answer.getAnswer());
					} else if (QuestionUtil.hasMultipleChoice(question)) {
						String[] answers = answer.getAnswer().split(",");
						Collections.addAll(answerList, answers);
					} else if (QuestionUtil.hasFillBlank(question) 
							|| (QuestionUtil.hasQA(question) && QuestionUtil.hasObjective(question))) {
						answerList.add(answer.getAnswer());
					}
				}
				questionMap.put("answers", answerList);
			}
			return PageResultEx.ok().data(questionMap);
		} catch (MyException e) {
			log.error("模拟练习试题错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("模拟练习试题错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 模拟练习试题列表
	 * 
	 * v1.0 zhanghc 2022年3月18日上午10:32:57
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/questionList")
	@ResponseBody
	public PageResult questionList(Integer id) {
		try {
			// 数据有效性校验
			Exer exer = exerService.getEntity(id);
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
			log.error("模拟练习试题列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("模拟练习试题列表错误：", e);
			return PageResult.err();
		}
	}
}
