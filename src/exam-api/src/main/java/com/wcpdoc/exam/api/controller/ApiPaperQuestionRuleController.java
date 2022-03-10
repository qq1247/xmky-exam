package com.wcpdoc.exam.api.controller;

import java.math.BigDecimal;

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
	@RequestMapping("/chapterAndRuleList")
	@ResponseBody
	public PageResult chapterAndRuleList(Integer paperId){
		try {
			return PageResultEx.ok().data(paperQuestionRuleService.chapterAndRuleList(paperId));
		} catch (MyException e) {
			log.error("随机章节规则列表错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("随机章节规则列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 更新随机章节规则
	 * 
	 * v1.0 chenyun 2022年3月9日下午5:31:08
	 * @param paperId
	 * @param chapterId
	 * @param questionTypeIds
	 * @param types
	 * @param difficultys
	 * @param ais
	 * @param scoreOptions
	 * @param nums
	 * @param scores
	 * @return PageResult
	 */
	@RequestMapping("/update")
	@ResponseBody
	public PageResult update(Integer paperId, Integer chapterId, Integer[] questionTypeIds, Integer[] types, String[] difficultys, String[] ais, String[] scoreOptions,  Integer[] nums,  BigDecimal[] scores ) {
		try {
			paperQuestionRuleService.update(paperId, chapterId, questionTypeIds, types, difficultys, ais, scoreOptions, nums, scores);
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
	 * 试题统计列表
	 * 
	 * v1.0 chenyun 2022年3月8日上午10:37:09
	 * @param questionTypeId
	 * @return PageResult
	 */
//	@RequestMapping("/questionList")
//	@ResponseBody
//	public PageResult questionList(Integer questionTypeId){
//		try {
//			return PageResultEx.ok().data(paperQuestionRuleService.questionListCache(questionTypeId));
//		} catch (MyException e) {
//			log.error("试题列表错误：{}", e.getMessage());
//			return PageResult.err().msg(e.getMessage());
//		} catch (Exception e) {
//			log.error("试题列表错误：", e);
//			return PageResult.err();
//		}
//	}
}