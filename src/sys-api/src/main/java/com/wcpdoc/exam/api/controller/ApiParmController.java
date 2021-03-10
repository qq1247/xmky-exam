package com.wcpdoc.exam.api.controller;

import java.util.Date;

import javax.annotation.Resource;

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
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(parmService.getListpage(pageIn));
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
	public PageResult get(Integer id) {
		try {
			Parm entity = parmService.getEntity(id);
			return PageResultEx.ok().data(entity);
		} catch (MyException e) {
			log.error("获取参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取参数错误：", e);
			return PageResult.err();
		}
	}
}
