package com.wcpdoc.exam.api.controller;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.report.service.GradeService;

/**
 * 成绩报表控制层
 * 
 * v1.0 zhanghc 2017年8月29日下午2:03:36
 */
@Controller
@RequestMapping("/api/gradeReport")
public class GradeReportController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(GradeReportController.class);
	
	@Resource
	private GradeService gradeService;
	
	/**
	 * 到达成绩列表页面 
	 * 
	 * v1.0 zhanghc 2017年8月29日下午2:03:36
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			model.addAttribute("MY_EXAM_STATE", DictCache.getIndexDictlistMap().get("MY_EXAM_STATE"));
			return "report/gradeReport/gradeReportList";
		} catch (Exception e) {
			log.error("到达成绩列表页面错误：", e);
			return "report/gradeReport/gradeReportList";
		}
	}
	
	/**
	 * 成绩列表 
	 * 
	 * v1.0 zhanghc 2017年8月29日下午2:03:36
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return gradeService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("成绩列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 分数统计
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/count")
	@ResponseBody
	@RequiresRoles(value={"user","subAdmin"},logical = Logical.OR)
	public PageResult count(Integer examId) {
		try {
			return PageResultEx.ok().data(gradeService.count(examId));
		} catch (MyException e) {
			log.error("分数统计错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("分数统计错误：", e);
			return PageResult.err();
		}
	}
}
