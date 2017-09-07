package com.wcpdoc.exam.viewlog.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.viewlog.service.LogService;

/**
 * 日志控制层
 * 
 * v1.0 zhanghc 2017年4月20日下午11:53:05
 */
@Controller
@RequestMapping("/log")
public class LogController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(LogController.class);
	
	@Resource
	private LogService logService;
	
	/**
	 * 到达日志列表页面 
	 * 
	 * v1.0 zhanghc 2017年4月20日下午11:53:05
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "/WEB-INF/jsp/viewlog/log/logList.jsp";
		} catch (Exception e) {
			log.error("到达日志列表页面错误：", e);
			return "/WEB-INF/jsp/viewlog/log/logList.jsp";
		}
	}
	
	/**
	 * 日志列表 
	 * 
	 * v1.0 zhanghc 2017年4月20日下午11:53:05
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return logService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("日志列表错误：", e);
			return new PageOut();
		}
	}
}
