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
import com.wcpdoc.exam.core.entity.RandChapterRules;
import com.wcpdoc.exam.core.service.RandChapterRulesService;

/**
 * 随机章节规则控制层
 * 
 * v1.0 chenyun 2022年2月14日下午3:25:46
 */
@Controller
@RequestMapping("/api/randChapterRules")
public class ApiRandChapterRulesController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiRandChapterRulesController.class);
	
	@Resource
	private RandChapterRulesService randChapterRulesService;
	
	/**
	 * 随机章节规则列表
	 * 
	 * v1.0 chenyun 2022年2月11日上午10:59:35
	 * @return pageOut
	 */
	@RequestMapping("/randChapterRulesList")
	@ResponseBody
	public PageResult randChapterRulesList(Integer paperId){
		try {
			return PageResultEx.ok().data(randChapterRulesService.randChapterRulesList(paperId));
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
	public PageResult add(RandChapterRules randChapterRules) {
		try {
			randChapterRulesService.addAndUpdate(randChapterRules);
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
	public PageResult edit(RandChapterRules randChapterRules) {
		try {
			randChapterRulesService.updateAndUpdate(randChapterRules);
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
	 * @param randChapterRulesId
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer[] ids) {
		try {
			randChapterRulesService.delAndUpdate(ids);
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
			RandChapterRules randChapterRules = randChapterRulesService.getEntity(id);
			Integer[] scoreOptions = null;
			if (ValidateUtil.isValid(randChapterRules.getScoreOptions())) {
				String[] split = randChapterRules.getScoreOptions().split(",");
				scoreOptions = new Integer[split.length];
				for(int i = 0; i < split.length; i++ ){
					scoreOptions[i] = Integer.parseInt(split[i]);
				}
			} else {
				scoreOptions = new Integer[0];
			}
			return PageResultEx.ok()
					.addAttr("id", randChapterRules.getId())
					.addAttr("paperId", randChapterRules.getPaperId())
					.addAttr("questionTypeId", randChapterRules.getQuestionTypeId())
					.addAttr("type", randChapterRules.getType())
					.addAttr("difficulty", randChapterRules.getDifficulty().split(","))
					.addAttr("ai", randChapterRules.getAi().split(","))
					.addAttr("scoreOptions", scoreOptions)
					.addAttr("totalNumber", randChapterRules.getTotalNumber())
					.addAttr("score", randChapterRules.getScore());
		} catch (MyException e) {
			log.error("获取随机章节规则错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取随机章节规则错误：", e);
			return PageResult.err();
		}
	}
}
