package com.wcpdoc.exam.home.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.util.DateUtil;
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
	@RequestMapping("/toIn")
	public String toIn(Model model) {
		try {
			return "/home/home/in";
		} catch (Exception e) {
			log.error("到达登录页面错误：", e);
			return "/home/home/in";
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
	@RequestMapping("/doIn")
	public String doIn(Model model, String loginName, String pwd) {
		try {
			//完成登录
			homeService.doIn(loginName, pwd, request);
			
			//跳转到首页。这里如果直接转向到home.jsp，页面刷新，会重复执行doIn()。
			return "redirect:/home/toHome";
		} catch (Exception e) {
			log.error("完成登录错误：{}", e.getMessage());
			try {
				model.addAttribute("message", URLEncoder.encode(e.getMessage(), "UTF-8") );
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			return "redirect:/home/toIn";
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
	@RequestMapping("/toHome")
	public String toHome(Model model) {
		try {
			return "/home/home/home";
		} catch (Exception e) {
			log.error("到达首页错误：", e);
			model.addAttribute("message", e.getMessage());
			return "redirect:/home/toIn";
		}
	}
	
	/**
	 * 完成退出登录
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @param loginName
	 * @return String
	 */
	@RequestMapping("/doOut")
	public String doOut(){
		try {
			//完成退出登录
			homeService.doOut(request);
			
			//重定向到登录页
			return "redirect:/home/toIn";
		} catch (Exception e) {
			log.error("完成退出登录错误：", e);
			return "redirect:/home/toIn";
		}
	}
	
	/**
	 * 获取服务器时间
	 * 
	 * v1.0 zhanghc 2017年6月25日下午4:19:05
	 * @return
	 * PageResult
	 */
	@RequestMapping("/sysTime")
	@ResponseBody
	public PageResult sysTime() {
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("sysTime", DateUtil.getFormatDateTime());
			return new PageResultEx(true, "获取成功", data);
		} catch (Exception e) {
			log.error("获取服务器时间错误：", e);
			return new PageResult(false, "获取失败：" + e.getMessage());
		}
	}
}
