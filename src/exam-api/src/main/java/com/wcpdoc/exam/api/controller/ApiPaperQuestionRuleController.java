package com.wcpdoc.exam.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.PaperQuestionRule;
import com.wcpdoc.exam.core.service.PaperQuestionRuleService;

/**
 * 随机章节规则控制层
 * 
 * v1.0 chenyun 2022年2月14日下午3:25:46
 */
@Controller
@RequestMapping("/api/paperQuestionRule")
public class ApiPaperQuestionRuleController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiPaperQuestionRuleController.class);
	
	@Resource
	private PaperQuestionRuleService paperQuestionRuleService;
	
	/**
	 * 随机章节规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午10:59:35
	 * @return pageOut
	 */
	@RequestMapping("/paperQuestionRuleList")
	@ResponseBody
	public PageResult paperQuestionRuleList(Integer paperId){
		try {
			return PageResultEx.ok().data(paperQuestionRuleService.paperQuestionRuleList(paperId));
		} catch (MyException e) {
			log.error("随机章节规则列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("随机章节规则列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加随机章节规则
	 * 
	 * v1.0 chenyun 2022年2月11日上午10:59:35
	 * @param chapter
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(PaperQuestionRule paperQuestionRule) {
		try {
			paperQuestionRuleService.addAndUpdate(paperQuestionRule);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加章节规则错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加随机章节规则错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改随机章节规则
	 * 
	 * v1.0 chenyun 2022年2月11日上午10:59:35
	 * @param chapter
	 * @return PageResult
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(PaperQuestionRule paperQuestionRule) {
		try {
			paperQuestionRuleService.updateAndUpdate(paperQuestionRule);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加随机章节规则错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加随机章节规则错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除随机章节规则
	 * 
	 * v1.0 chenyun 2022年2月11日下午4:04:19
	 * @param paperQuestionRuleId
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer[] ids) {
		try {
			paperQuestionRuleService.delAndUpdate(ids);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("随机章节规则删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("随机章节规则删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取随机章节规则
	 * 
	 * v1.0 chenyun 2021年3月29日下午5:40:20
	 * @param paperId
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			PaperQuestionRule paperQuestionRule = paperQuestionRuleService.getEntity(id);
			Integer[] scoreOptions = null;
			if (ValidateUtil.isValid(paperQuestionRule.getScoreOptions())) {
				String[] split = paperQuestionRule.getScoreOptions().split(",");
				scoreOptions = new Integer[split.length];
				for(int i = 0; i < split.length; i++ ){
					scoreOptions[i] = Integer.parseInt(split[i]);
				}
			} else {
				scoreOptions = new Integer[0];
			}
			return PageResultEx.ok()
					.addAttr("id", paperQuestionRule.getId())
					.addAttr("paperId", paperQuestionRule.getPaperId())
					.addAttr("questionTypeId", paperQuestionRule.getQuestionTypeId())
					.addAttr("type", paperQuestionRule.getType())
					.addAttr("difficultys", paperQuestionRule.getDifficultyArr())
					.addAttr("ais", paperQuestionRule.getAiArr())
					.addAttr("scoreOptions", scoreOptions)
					.addAttr("num", paperQuestionRule.getNum())
					.addAttr("score", paperQuestionRule.getScore());
		} catch (MyException e) {
			log.error("获取随机章节规则错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取随机章节规则错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 试题列表
	 * 
	 * v1.0 chenyun 2022年3月8日上午10:37:09
	 * @param questionTypeId
	 * @return PageResult
	 */
	@RequestMapping("/questionList")
	@ResponseBody
	public PageResult questionList(Integer questionTypeId){
		try {
			return PageResultEx.ok().data(paperQuestionRuleService.questionListCache(questionTypeId));
		} catch (MyException e) {
			log.error("试题列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return PageResult.err();
		}
	}
}