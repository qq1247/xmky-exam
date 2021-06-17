package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.entity.Parm;
import com.wcpdoc.exam.base.service.ParmService;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
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
	 * 参数列表
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult listpage() {
		try {
			return PageResultEx.ok().data(parmService.getListpage(new PageIn(request)));
		} catch (Exception e) {
			log.error("参数列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加参数
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult add(Parm email) {
		try {
			email.setUpdateTime(new Date());
			email.setUpdateUserId(getCurUser().getId());
			parmService.addAndUpdate(email);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加参数错误：", e);
			return PageResult.err();
		}
	}
	
	
	/**
	 * 修改参数
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult edit(Parm parm) {
		try {
			Parm entity = parmService.getEntity(parm.getId());
			entity.setEmailHost(parm.getEmailHost());
			entity.setEmailUserName(parm.getEmailUserName());
			entity.setEmailPwd(parm.getEmailPwd());
			entity.setEmailProtocol(parm.getEmailProtocol());
			entity.setEmailEncode(parm.getEmailEncode());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			parmService.updateAndUpdate(entity);
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
	 * 获取参数
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult get(Integer id) {
		try {
			Parm entity = parmService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("emailHost", entity.getEmailHost())
					.addAttr("emailEncode", entity.getEmailEncode())
					.addAttr("emailUserName", entity.getEmailUserName())
					.addAttr("emailPwd", entity.getEmailPwd())
					.addAttr("emailProtocol", entity.getEmailProtocol())
					.addAttr("orgLogo", entity.getOrgLogo())
					.addAttr("orgName", entity.getOrgName());
		} catch (MyException e) {
			log.error("获取参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取参数错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除参数
	 * 
	 * v1.0 zhanghc 2016-6-15下午17:24:19
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult del(Integer id) {
		try {
			parmService.del(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除参数错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 自定义logo、单位名称
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/editLogo")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult editLogo(Parm parm) {
		try {
			Parm entity = parmService.getEntity(parm.getId());
			entity.setOrgLogo(parm.getOrgLogo());
			entity.setOrgName(parm.getOrgName());
			parmService.updateAndUpdate(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改参数错误：", e);
			return PageResult.err();
		}
	}
}
