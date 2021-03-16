package com.wcpdoc.exam.web.controller;

import java.util.Date;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
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
	 * 完成登录
	 * 
	 * v1.0 zhanghc 2016年7月18日下午3:23:00
	 * @param loginName
	 * @param pwd
	 * @param model
	 * @return String
	 */
	@RequestMapping("/in")
	@ResponseBody
	public PageResult in(String loginName, String pwd) {
		try {
			//完成登录
			String token = loginService.in(loginName, pwd);
			return PageResultEx.ok().data(token);
		} catch (LoginException e) {
			log.error("完成登录错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成登录错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 退出登录
	 * v1.0 zhanghc 2016年9月8日下午8:50:37
	 * @return String
	 */
	@RequestMapping("/out")
	@ResponseBody
	public PageResult out() {
		try {
			//完成退出登录
			loginService.out();
			return PageResult.ok();
		} catch (Exception e) {
			log.error("退出登录错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改密码
	 * 
	 * v1.0 zhanghc 2017年7月14日下午3:05:21
	 * @param oldPwd
	 * @param newPwd
	 * @return PageResult
	 */
	@RequestMapping("/pwdUpdate")
	@ResponseBody
	public PageResult pwdUpdate(String oldPwd, String newPwd) {
		try {
			loginService.pwdUpdate(oldPwd, newPwd);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改密码错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改密码错误：", e);
			return PageResult.err();
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
			return PageResultEx.ok().data(DateUtil.formatDateTime(new Date()));
		} catch (Exception e) {
			log.error("获取服务器时间错误：", e);
			return PageResult.err().msg(e.getMessage());
		}
	}
}
