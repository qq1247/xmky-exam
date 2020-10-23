package com.wcpdoc.exam.web.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.service.UserService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.web.service.LoginService;

/**
 * 登录控制层
 * 
 * v1.0 zhanghc 2016年7月10日下午11:44:41
 */
@Controller
@RequestMapping("/login")
public class LoginController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(LoginController.class);
	
	@Resource
	private LoginService loginService;
	@Resource
	private UserService userService;
	
	/**
	 * 到达登录页面
	 * 
	 * v1.0 zhanghc 2016年9月8日下午8:47:13
	 * @return String
	 */
	@RequestMapping("/toIn")
	public String toIn(Model model) {
		try {
			return "web/login/in";
		} catch (Exception e) {
			log.error("到达登录页面错误：", e);
			return "web/login/in";
		}
	}
	
	/**
	 * 完成登录
	 * 
	 * v1.0 zhanghc 2016年7月18日下午3:23:00
	 * @param loginName
	 * @param pwd
	 * @param model
	 * @return String
	 */
	@RequestMapping("/doIn")
	public String doIn(Model model, String loginName, String pwd) {
		try {
			//完成登录
			loginService.doIn(loginName, pwd, request);
			//重定向到首页
			return "redirect:/login/toHome";
		} catch (LoginException e) {
			log.error("完成登录错误：{}", e.getMessage());
			try {
				return "redirect:/login/toIn?message=" + URLEncoder.encode(e.getMessage(), "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				return "redirect:/login/toIn";
			}
		} catch (Exception e) {
			log.error("完成登录错误：", e);
			try {
				return "redirect:/login/toIn?message=" + URLEncoder.encode("未知异常！", "UTF-8");
			} catch (Exception e1) {
				return "redirect:/login/toIn";
			}
		}
	}
	
	/**
	 * 到达首页
	 * 
	 * @param model
	 * v1.0 zhanghc 2018年11月25日下午2:15:38
	 * @return String
	 */
	@RequestMapping("/toHome")
	public String toHome(Model model) {
		try {
			if(getCurUser() == null) {
				return "redirect:/login/toIn";
			}
			
			return "web/login/home";
		} catch (Exception e) {
			log.error("到达首页错误：", e);
			return "web/login/home";
		}
	}
	
	/**
	 * 完成退出登录
	 * v1.0 zhanghc 2016年9月8日下午8:50:37
	 * @return String
	 */
	@RequestMapping("/doOut")
	public String doOut() {
		try {
			//完成退出登录
			loginService.doOut(request);
			
			//重定向到登录页
			return "redirect:/login/toIn";
		} catch (Exception e) {
			log.error("完成退出登录错误：", e);
			return "redirect:/login/toIn";
		}
	}
	
	/**
	 * 到达修改密码页面
	 * 
	 * v1.0 zhanghc 2017年7月14日下午4:24:33
	 * @return String
	 */
	@RequestMapping("/toPwdUpdate")
	public String toPwdUpdate() {
		try {
			return "web/login/pwdUpdate";
		} catch (Exception e) {
			log.error("到达修改密码页面错误：", e);
			return "web/login/pwdUpdate";
		}
	}
	
	/**
	 * 完成修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午3:05:21
	 * @param oldPwd
	 * @param newPwd
	 * @return PageResult
	 */
	@RequestMapping("/doPwdUpdate")
	@ResponseBody
	public PageResult doPwdUpdate(String oldPwd, String newPwd) {
		try {
			loginService.doPwdUpdate(oldPwd, newPwd);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("修改密码错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("修改密码错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
	
	/**
	 * 获取服务器时间
	 * 
	 * v1.0 zhanghc 2017年6月25日下午4:19:05
	 * @return
	 * PageResult
	 */
	@RequestMapping("/curTime")
	@ResponseBody
	public PageResult sysTime() {
		try {
			return new PageResultEx(true, "获取成功", DateUtil.formatDateTime(new Date()));
		} catch (Exception e) {
			log.error("获取服务器时间错误：", e);
			return new PageResult(false, e.getMessage());
		}
	}
}
