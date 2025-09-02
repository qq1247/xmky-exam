package com.wcpdoc.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.BaseCacheService;
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
	@Resource
	private BaseCacheService baseCacheService;

	/**
	 * 系统修改
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * 
	 * @param name
	 * @param logoFileId
	 * @return PageResult
	 */
	@RequestMapping("/sys")
	public PageResult ent(String name, Integer logoFileId) {
		try {
			parmService.ent(logoFileId, name);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("系统修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("系统修改错误：", e);
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
			parmService.email(host, userName, pwd, protocol, encode);
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
			parmService.file(uploadDir);
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
			parmService.db(bakDir);
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
			parmService.pwd(type, value);
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
	 * 服务支持修改
	 * 
	 * v1.0 zhanghc 2023年3月10日上午9:22:54
	 * 
	 * @param title
	 * @param content
	 * @return PageResult
	 */
	@RequestMapping("/support")
	public PageResult support(String title, String content) {
		try {
			parmService.support(title, content);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("服务支持修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("服务支持修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 备案信息修改
	 * 
	 * v1.0 zhanghc 2025年9月1日下午2:33:37
	 * 
	 * @param title
	 * @param content
	 * @return PageResult
	 */
	@RequestMapping("/icp")
	public PageResult icp(String icp) {
		try {
			parmService.icp(icp);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("备案信息修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("备案信息修改错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 移动端设置
	 * 
	 * v1.0 zhanghc 2024年9月30日下午1:03:10
	 * 
	 * @param host
	 * @return PageResult
	 */
	@RequestMapping("/m")
	public PageResult m(String host) {
		try {
			parmService.m(host);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("移动端设置修改错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("移动端设置修改错误：", e);
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
			Parm parm = baseCacheService.getParm();
			;
			return PageResultEx.ok()//
					.addAttr("sysName", parm.getSysName())//
					.addAttr("fileUploadDir", parm.getFileUploadDir())//
					.addAttr("dbBakDir", parm.getDbBakDir())//
					.addAttr("pwdType", parm.getPwdType())//
					.addAttr("pwdValue", parm.getPwdValue())//
					.addAttr("customTitle", parm.getCustomTitle())//
					.addAttr("customContent", parm.getCustomContent())//
					.addAttr("customContent", parm.getCustomContent())//
					.addAttr("icp", parm.getIcp())//
					.addAttr("mHost", parm.getMHost())//
			;
		} catch (MyException e) {
			log.error("参数获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("参数获取错误：", e);
			return PageResult.err();
		}
	}
}
