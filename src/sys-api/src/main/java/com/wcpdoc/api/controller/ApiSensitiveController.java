package com.wcpdoc.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.wordFilter.entity.Sensitive;
import com.wcpdoc.wordFilter.service.SensitiveService;

/**
 * 敏感词控制层
 * 
 * v1.0 chenyun 2021-03-24 13:39:37
 */
@Controller
@RequestMapping("/api/sensitive")
public class ApiSensitiveController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiSensitiveController.class);
	
	@Resource
	private SensitiveService sensitiveService;
	
	/**
	 * 修改敏感词
	 * 
	 * v1.0 chenyun 2021-03-24 13:39:37
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Sensitive sensitive) {
		try {
			sensitiveService.updateEx(sensitive);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改敏感词错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改敏感词错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取敏感词
	 * 
	 * v1.0 chenyun 2021-03-04 15:02:18
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {		
		try {
			Sensitive entity = sensitiveService.getEntity(1);
			if (entity == null) {
				return PageResultEx.ok();
			}
			return PageResultEx.ok()
					.addAttr("blackList", entity.getBlackList())
					.addAttr("whiteList", entity.getWhiteList());
		} catch (MyException e) {
			log.error("获取参数错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取参数错误：", e);
			return PageResult.err();
		}
	}
}
