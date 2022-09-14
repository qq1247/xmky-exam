package com.wcpdoc.exam.api.controller;

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
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 题库控制层
 * 
 * v1.0 zhanghc 2016-5-24下午14:54:09
 */
@Controller
@RequestMapping("/api/questionType")
public class ApiQuestionTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionTypeController.class);
	
	@Resource
	private QuestionTypeService questionTypeService;
	
	/**
	 * 题库列表 
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = questionTypeService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("题库列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 题库添加
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(QuestionType questionType) {
		try {
			questionTypeService.addAndUpdate(questionType);
			return PageResultEx.ok().data(questionType.getId());
		} catch (MyException e) {
			log.error("题库添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库添加错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 题库修改
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(QuestionType questionType) {
		try {
			questionTypeService.editAndUpdate(questionType);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("题库修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库修改错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 题库删除
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			questionTypeService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("题库删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 题库获取
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			QuestionType entity = questionTypeService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("name", entity.getName())
					.addAttr("updateTime", DateUtil.formatDateTime(entity.getUpdateTime()));
		} catch (MyException e) {
			log.error("题库获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("题库获取错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 题库合并
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/move")
	@ResponseBody
	public PageResult move(Integer sourceId, Integer targetId) {
		try {
			questionTypeService.move(sourceId, targetId);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("题库合并错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		}  catch (Exception e) {
			log.error("题库合并错误：", e);
			return PageResult.err();
		}
	}
}
