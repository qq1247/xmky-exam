package com.wcpdoc.exam.base.controller;

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
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.exception.MyException;
/**
 * 参数控制层
 * 
 * v1.0 chenyun 2021-03-04 15:02:18
 */
@Controller
@RequestMapping("/parm")
public class ParmController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ParmController.class);
	
	@Resource
	private ParmService parmService;
	
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
			Parm entity = parmService.getEntity(1);
			entity.setEmailHost(parm.getEmailHost());
			entity.setEmailUserName(parm.getEmailUserName());
			entity.setEmailPwd(parm.getEmailPwd());
			entity.setEmailEncode(parm.getEmailEncode());
			entity.setEmailProtocol(parm.getEmailProtocol());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
			parmService.updateAndUpdate(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改参数错误：", e);
			return PageResult.err();
		}
	}
}
