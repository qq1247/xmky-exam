package com.wcpdoc.api.controller;

import java.io.File;
import java.util.Date;

import javax.annotation.Resource;
import javax.security.auth.login.LoginException;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.api.entity.UserToken;
import com.wcpdoc.api.service.LoginService;
import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录控制层
 * 
 * v1.0 zhanghc 2016年7月10日下午11:44:41
 */
@RestController
@RequestMapping("/api/login")
@Slf4j
public class ApiLoginController extends BaseController {

	@Resource
	private LoginService loginService;

	/**
	 * 完成登录
	 * 
	 * v1.0 zhanghc 2016年7月18日下午3:23:00
	 * 
	 * @param loginName
	 * @param pwd
	 * @param model
	 * @return String
	 */
	@RequestMapping("/in")
	public PageResult in(String loginName, String pwd) {
		try {
			// 完成登录
			UserToken userToken = loginService.in(loginName, pwd);
			return PageResultEx.ok().data(userToken);
		} catch (LoginException e) {
			log.error("完成登录错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成登录错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 退出登录 v1.0 zhanghc 2016年9月8日下午8:50:37
	 * 
	 * @return String
	 */
	@RequestMapping("/out")
	public PageResult out() {
		try {
			// 完成退出登录
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
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @return PageResult
	 */
	@RequestMapping("/pwd")
	public PageResult pwd(String oldPwd, String newPwd) {
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
	 * v1.0 zhanghc 2017年6月25日下午4:19:05 每隔30秒前端请求一次，心跳机制检测在线状态
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/sysTime")
	public PageResult sysTime() {
		try {
			return PageResultEx.ok().data(DateUtil.formatDateTime(new Date()));
		} catch (Exception e) {
			log.error("获取服务器时间错误：", e);
			return PageResult.err().msg(e.getMessage());
		}
	}

	/**
	 * 获取企业信息
	 * 
	 * v1.0 chenyun 2021-10-08 16:05:35
	 * 
	 * @return void
	 */
	@RequestMapping("/ent")
	public PageResult ent() {
		try {
			Parm parm = ParmCache.get();
			return PageResultEx.ok().addAttr("name", parm.getEntName());
		} catch (Exception e) {
			log.error("获取企业名称错误：", e);
			return PageResult.err().msg(e.getMessage());
		}
	}

	/**
	 * 获取自定义信息
	 * 
	 * v1.0 zhanghc 2023年3月10日上午11:36:23
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/custom")
	public PageResult custom() {
		try {
			Parm parm = ParmCache.get();
			return PageResultEx.ok().addAttr("title", parm.getCustomTitle()).addAttr("content",
					parm.getCustomContent());
		} catch (Exception e) {
			log.error("获取自定义错误：", e);
			return PageResult.err().msg(e.getMessage());
		}
	}

	/**
	 * 获取企业logo
	 * 
	 * v1.0 chenyun 2021-10-08 16:05:35
	 * 
	 * @return void
	 */
	@RequestMapping("/logo")
	public void logo() {
		try {
			File logo = new File("./config/logo.png");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + new String("logo.png".getBytes("UTF-8"), "ISO-8859-1"));
			response.setContentType("application/force-download");
			FileUtils.copyFile(logo, response.getOutputStream());
		} catch (MyException e) {
			log.error("获取企业logo失败：{}", e.getMessage());
		} catch (Exception e) {
			log.error("获取企业logo失败：", e);
		}
	}
}
