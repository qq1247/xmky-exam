package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
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
@RequestMapping("/api/questionTypeOpen")
public class ApiQuestionTypeOpenController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionTypeOpenController.class);
	
	@Resource
	private QuestionTypeOpenService questionTypeOpenService;
	
	/**
	 * 试题分类列表
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult listpage(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(questionTypeOpenService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题分类列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成添加试题分类
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult add(QuestionTypeOpen questionTypeOpen) {
		try {
			questionTypeOpen.setUpdateUserId(getCurUser().getId());
			questionTypeOpen.setUpdateTime(new Date());
			questionTypeOpenService.add(questionTypeOpen);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成修改试题分类
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult edit(QuestionTypeOpen questionTypeOpen) {
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
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试题分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成删除试题分类
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	@RequiresRoles("OP")
	public PageResult del(Integer id) {
		try {
			questionTypeOpenService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除试题分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试题分类错误：", e);
			return PageResult.err();
		}
	}
}
