package com.wcpdoc.exam.home.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.exam.service.ExamService;
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
	@Resource
	private ExamService examService;
	
	/**
	 * 到达登录页面
	 * 
	 * v1.0 zhanghc 2017-06-23 15:39:11
	 * @return String
	 */
	@RequestMapping("/toIn")
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
			PageIn pageIn = new PageIn();
			pageIn.setRows(100);
			pageIn.setNine(getCurrentUser().getId() + "");
			List<Map<String, Object>> myExamList = examService.getListpage(pageIn).getRows();
			ListIterator<Map<String, Object>> listIterator = myExamList.listIterator();
			Date curTime = new Date();
			while(listIterator.hasNext()){
				Map<String, Object> next = listIterator.next();
				if((int)next.get("STATE") != 1){
					listIterator.remove();
					continue;
				}
				Date startEndTime = (Date) next.get("START_TIME");
				Date examEndTime = (Date) next.get("END_TIME");
				if(!(startEndTime.getTime() <= curTime.getTime() && examEndTime.getTime() >= curTime.getTime())){
					listIterator.remove();
					continue;
				}
			}
			model.addAttribute("myExamList", myExamList);
			
			pageIn = new PageIn();
			pageIn.setRows(100);
			pageIn.setTen(getCurrentUser().getId().toString());
			pageIn.setFour("1");
			List<Map<String, Object>> myMarkList = examService.getListpage(pageIn).getRows();
			listIterator = myMarkList.listIterator();
			while(listIterator.hasNext()){
				Map<String, Object> next = listIterator.next();
				
				if((int)next.get("STATE") != 1){
					listIterator.remove();
					continue;
				}
				Date markStartTime = (Date) next.get("MARK_START_TIME");
				Date markEndTime = (Date) next.get("MARK_END_TIME");
				if(!(markStartTime.getTime() <= curTime.getTime() && markEndTime.getTime() >= curTime.getTime())){
					listIterator.remove();
					continue;
				}
			}
			model.addAttribute("myMarkList", myMarkList);
			return "/WEB-INF/jsp/home/home/home.jsp";
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
