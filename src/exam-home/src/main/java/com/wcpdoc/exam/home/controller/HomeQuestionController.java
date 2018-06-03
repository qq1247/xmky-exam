package com.wcpdoc.exam.home.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.home.service.HomeQuestionService;

/**
 * 试题控制层
 * 
 * v1.0 zhanghc 2018年6月3日上午10:34:21
 */
@Controller
@RequestMapping("/home/question")
public class HomeQuestionController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(HomeQuestionController.class);
	
	@Resource
	private HomeQuestionService homeQuestionService;
	
	/**
	 * 到达试题列表页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			return "/WEB-INF/jsp/home/question/questionList.jsp";
		} catch (Exception e) {
			log.error("到达试题列表页面错误：", e);
			return "/WEB-INF/jsp/home/question/questionList.jsp";
		}
	}
	
	/**
	 * 获取试题分类数据
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/questionTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> questionTypeTreeList() {
		try {
			LoginUser user = getCurrentUser();
			return homeQuestionService.getQuestionTypeTreeList(user.getId());
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
}
