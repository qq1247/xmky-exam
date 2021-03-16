package com.wcpdoc.exam.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.QuestionOption;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionOptionService;
/**
 * 试题选项控制层
 * 
 * v1.0 chenyun 2021-03-10 16:11:06
 */
@Controller
@RequestMapping("/api/questionOption")
public class ApiQuestionOptionController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiQuestionOptionController.class);
	
	@Resource
	private QuestionOptionService questionOptionService;
	
	/**
	 * 试题选项列表
	 * 
	 * v1.0 chenyun 2021-03-10 16:11:06
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(questionOptionService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题选项列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加试题选项
	 * 
	 * v1.0 chenyun 2021-03-10 16:11:06
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(QuestionOption questionOption) {
		try {
			questionOptionService.add(questionOption);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加试题选项错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加试题选项错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改试题选项
	 * 
	 * v1.0 chenyun 2021-03-10 16:11:06
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(QuestionOption questionOption) {
		try {
			QuestionOption entity = questionOptionService.getEntity(questionOption.getId());
			entity.setOptionA(questionOption.getOptionA());
			entity.setOptionB(questionOption.getOptionB());
			entity.setOptionC(questionOption.getOptionC());
			entity.setOptionD(questionOption.getOptionD());
			entity.setOptionE(questionOption.getOptionE());
			entity.setOptionF(questionOption.getOptionF());
			entity.setOptionG(questionOption.getOptionG());
			entity.setQuestionId(questionOption.getQuestionId());
			questionOptionService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改试题选项错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改试题选项错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除试题选项
	 * 
	 * v1.0 chenyun 2021-03-10 16:11:06
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			questionOptionService.del(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除试题选项错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除试题选项错误：", e);
			return PageResult.err();
		}
	}
}
