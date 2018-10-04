package com.wcpdoc.exam.home.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.home.service.HomeService;

/**
 * 首页控制层 
 * 
 * v1.0 zhanghc 2017-06-23 15:39:11
 */
@Controller
@RequestMapping("/home")
public class HomeController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	@Resource
	private HomeService homeService;
	
	/**
	 * 到达登录页面
	 * 
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @return String
	 */
	@RequestMapping("/pubToIn")
	public String toIn(Model model) {
		try {
			return "/WEB-INF/jsp/home/home/in.jsp";
		} catch (Exception e) {
			log.error("到达登录页面错误：", e);
			return "/WEB-INF/jsp/home/home/in.jsp";
		}
	}
	
	/**
	 * 完成登录
	 * 
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @param loginName
	 * @param pwd
	 * @param model
	 * @return String
	 */
	@RequestMapping("/pubDoIn")
	public String doIn(Model model, String loginName, String pwd) {
		try {
			//完成登录
			homeService.doIn(loginName, pwd, request);
			
			//跳转到首页。这里如果直接转向到home.jsp，页面刷新，会重复执行doIn()。
			return "redirect:/home/pubToHome";
		} catch (Exception e) {
			log.error("完成登录错误：{}", e.getMessage());
			try {
				model.addAttribute("message", URLEncoder.encode(e.getMessage(), "UTF-8") );
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			return "redirect:/home/pubToIn";
		}
	}
	
	/**
	 * 到达首页
	 * 
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @param loginName
	 * @param pwd
	 * @param model
	 * @return String
	 */
	@RequestMapping("/pubToHome")
	public String home(Model model) {
		try {
//			List<Map<String, Object>> unFinishExamList = homeService.getUnFinishExamList(getCurrentUser().getId());
//			model.addAttribute("unFinishExamList", unFinishExamList);
			return "/WEB-INF/jsp/home/home/home.jsp";
		} catch (Exception e) {
			log.error("到达首页错误：", e);
			model.addAttribute("message", e.getMessage());
			return "redirect:/home/pubToIn";
		}
	}
	
	/**
	 * 完成退出登录
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @param loginName
	 * @return String
	 */
	@RequestMapping("/pubDoOut")
	public String doOut(){
		try {
			//完成退出登录
			homeService.doOut(request);
			
			//重定向到登录页
			return "redirect:/home/pubToIn";
		} catch (Exception e) {
			log.error("完成退出登录错误：", e);
			return "redirect:/home/pubToIn";
		}
	}
}
