package com.wcpdoc.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
/**
 * 参数控制层
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Controller
@RequestMapping("/api/parm")
public class ApiParmController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiParmController.class);
	
	@Resource
	private ParmService parmService;
	
	/**
	 * 修改logo
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/logo")
	@ResponseBody
	public PageResult logo(Parm parm) {
		try {
			parmService.logoUpdate(parm);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改参数错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改邮件
	 * 
	 * v1.0 wjj 2021年11月8日下午1:25:33
	 * 
	 * @param parm
	 * @return PageResult
	 */
	@RequestMapping("/email")
	@ResponseBody
	public PageResult email(String host, String userName, String pwd, String protocol, String encode) {
		try {
			parmService.emailUpdate(host, userName, pwd, protocol, encode);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加邮件错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加邮件错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 修改上传目录
	 * 
	 * v1.0 chenyun 2021-11-12 10:34:15
	 * 
	 * @param uploadDir
	 * @return PageResult
	 */
	@RequestMapping("/file")
	@ResponseBody
	public PageResult file(String uploadDir) {
		try {
			parmService.fileUpdate(uploadDir);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("上传附件目录错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("上传附件目录错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 修改DB目录
	 * 
	 * v1.0 chenyun 2021-11-12 10:34:15
	 * 
	 * @param bakDir
	 * @return PageResult
	 */
	@RequestMapping("/db")
	@ResponseBody
	public PageResult db(String bakDir) {
		try {
			parmService.dbUpdate(bakDir);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("数据库备份目录错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("数据库备份目录错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改默认密码
	 * 
	 * v1.0 chenyun 2021-11-12 10:08:32
	 * 
	 * @param type
	 * @param value
	 * @return PageResult
	 */
	@RequestMapping("/pwd")
	@ResponseBody
	public PageResult pwd(Integer type, String value) {
		try {
			parmService.pwdUpdate(type, value);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("设置参数密码初始化错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("设置参数密码初始化错误：", e);
			return PageResult.err();
		}
	}
		
	/**
	 *  获取参数
	 * 
	 * v1.0 chenyun 2021年11月12日下午3:38:42
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get() {
		try {
			Parm parm = parmService.getEntity(1);
			return PageResultEx.ok()
					.addAttr("emailHost", parm.getEmailHost())
					.addAttr("emailUserName", parm.getEmailUserName())
					.addAttr("emailPwd", parm.getEmailPwd())
					.addAttr("emailProtocol", parm.getEmailProtocol())
					.addAttr("emailEncode", parm.getEmailEncode())
					.addAttr("orgLogo", parm.getOrgLogo())
					.addAttr("orgName", parm.getOrgName())
					.addAttr("fileUploadDir", parm.getFileUploadDir())
					.addAttr("dbBakDir", parm.getDbBakDir())
					.addAttr("pwdType", parm.getPwdType())
					.addAttr("pwdValue", parm.getPwdValue());
		} catch (MyException e) {
			log.error("获取参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取参数错误：", e);
			return PageResult.err();
		}
	}
}
