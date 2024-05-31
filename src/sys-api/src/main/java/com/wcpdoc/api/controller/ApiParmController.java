package com.wcpdoc.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;

import lombok.extern.slf4j.Slf4j;

/**
 * 参数控制层
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@RestController
@RequestMapping("/api/parm")
@Slf4j
public class ApiParmController extends BaseController {

	@Resource
	private ParmService parmService;

	/**
	 * 企业修改
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * 
	 * @param logoFileId
	 * @param name
	 * @return PageResult
	 */
	@RequestMapping("/ent")
	public PageResult ent(Integer logoFileId, String name) {
		try {
			parmService.entUpdate(logoFileId, name);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("企业修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("企业修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 邮件修改
	 * 
	 * v1.0 wjj 2021年11月8日下午1:25:33
	 * 
	 * @param parm
	 * @return PageResult
	 */
	@RequestMapping("/email")
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
	 * 上传目录修改
	 * 
	 * v1.0 chenyun 2021-11-12 10:34:15
	 * 
	 * @param uploadDir
	 * @return PageResult
	 */
	@RequestMapping("/file")
	public PageResult file(String uploadDir) {
		try {
			parmService.fileUpdate(uploadDir);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("上传目录修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("上传目录修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * DB目录修改
	 * 
	 * v1.0 chenyun 2021-11-12 10:34:15
	 * 
	 * @param bakDir
	 * @return PageResult
	 */
	@RequestMapping("/db")
	public PageResult db(String bakDir) {
		try {
			parmService.dbUpdate(bakDir);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("DB目录修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("DB目录修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 默认密码修改
	 * 
	 * v1.0 chenyun 2021-11-12 10:08:32
	 * 
	 * @param type
	 * @param value
	 * @return PageResult
	 */
	@RequestMapping("/pwd")
	public PageResult pwd(Integer type, String value) {
		try {
			parmService.pwdUpdate(type, value);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("默认密码修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("默认密码修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 自定义信息修改
	 * 
	 * v1.0 zhanghc 2023年3月10日上午9:22:54
	 * 
	 * @param title
	 * @param content
	 * @return PageResult
	 */
	@RequestMapping("/custom")
	public PageResult custom(String title, String content) {
		try {
			parmService.customUpdate(title, content);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("自定义信息修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("自定义信息修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 参数获取
	 * 
	 * v1.0 chenyun 2021年11月12日下午3:38:42
	 * 
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get() {
		try {
			Parm parm = parmService.getById(1);
			return PageResultEx.ok().addAttr("entName", parm.getEntName())
					.addAttr("fileUploadDir", parm.getFileUploadDir()).addAttr("dbBakDir", parm.getDbBakDir())
					.addAttr("pwdType", parm.getPwdType()).addAttr("pwdValue", parm.getPwdValue())
					.addAttr("customTitle", parm.getCustomTitle()).addAttr("customContent", parm.getCustomContent());
		} catch (MyException e) {
			log.error("参数获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("参数获取错误：", e);
			return PageResult.err();
		}
	}
}
