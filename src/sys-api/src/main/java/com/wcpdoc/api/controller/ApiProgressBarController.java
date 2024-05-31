package com.wcpdoc.api.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.entity.ProgressBar;
import com.wcpdoc.base.service.ProgressBarService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;

import lombok.extern.slf4j.Slf4j;

/**
 * 进度条控制层
 * 
 * v1.0 zhanghc 2020年10月13日下午5:39:55
 */
@RestController
@RequestMapping("/api/progressBar")
@Slf4j
public class ApiProgressBarController extends BaseController {
	@Resource
	private ProgressBarService progressBarService;

	/**
	 * 进度条获取
	 * 
	 * v1.0 zhanghc 2020年10月13日下午5:39:59
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(String id) {
		try {
			ProgressBar progressBar = progressBarService.getProgressBar(id);
			return PageResultEx.custom()//
					.addAttr("curNum", progressBar.getCurNum())//
					.addAttr("totalNum", progressBar.getTotalNum())//
					.addAttr("percent", progressBar.getPercent())//
					.code(progressBar.getCode())//
					.msg(progressBar.getMsg());
		} catch (MyException e) {
			log.error("进度条获取错误：{}", e.getMessage());
			return PageResultEx.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("进度条获取错误：", e);
			return PageResultEx.err();
		}
	}
}
