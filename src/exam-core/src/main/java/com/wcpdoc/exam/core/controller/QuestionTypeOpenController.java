package com.wcpdoc.exam.core.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.QuestionTypeOpen;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionTypeOpenService;
/**
 * 试题分类控制层
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Controller
@RequestMapping("/questionTypeOpen")
public class QuestionTypeOpenController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(QuestionTypeOpenController.class);
	
	@Resource
	private QuestionTypeOpenService questionTypeOpenService;
	
	/**
	 * 到达试题分类列表页面
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "core/questionTypeOpen/questionTypeOpenList";
		} catch (Exception e) {
			log.error("到达试题分类列表页面错误：", e);
			return "core/questionTypeOpen/questionTypeOpenList";
		}
	}
	
	/**
	 * 试题分类列表
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return new PageResultEx(true, "查询成功", questionTypeOpenService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题分类列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 到达添加试题分类页面
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			return "core/questionTypeOpen/questionTypeOpenEdit";
		} catch (Exception e) {
			log.error("到达添加试题分类页面错误：", e);
			return "core/questionTypeOpen/questionTypeOpenEdit";
		}
	}
	
	/**
	 * 完成添加试题分类
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(QuestionTypeOpen questionTypeOpen) {
		try {
			questionTypeOpen.setUpdateUserId(getCurUser().getId());
			questionTypeOpen.setUpdateTime(new Date());
			questionTypeOpenService.add(questionTypeOpen);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加试题分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试题分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 到达修改试题分类页面
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			QuestionTypeOpen questionTypeOpen = questionTypeOpenService.getEntity(id);
			model.addAttribute("questionTypeOpen", questionTypeOpen);
			return "core/questionTypeOpen/questionTypeOpenEdit";
		} catch (Exception e) {
			log.error("到达修改试题分类页面错误：", e);
			return "core/questionTypeOpen/questionTypeOpenEdit";
		}
	}
	
	/**
	 * 完成修改试题分类
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(QuestionTypeOpen questionTypeOpen) {
		try {
			QuestionTypeOpen entity = questionTypeOpenService.getEntity(questionTypeOpen.getId());
			entity.setStartTime(questionTypeOpen.getStartTime());
			entity.setEndTime(questionTypeOpen.getEndTime());
			entity.setUserIds(questionTypeOpen.getUserIds());
			entity.setOrgIds(questionTypeOpen.getOrgIds());
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			entity.setState(questionTypeOpen.getState());
			entity.setQuestionTypeId(questionTypeOpen.getQuestionTypeId());
			questionTypeOpenService.update(entity);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("完成修改试题分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试题分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 完成删除试题分类
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			questionTypeOpenService.delAndUpdate(id);
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除试题分类错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试题分类错误：", e);
			return new PageResult(false, "未知异常");
		}
	}

}
