package com.wcpdoc.exam.base.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.cache.ProgressBarCache;
import com.wcpdoc.exam.base.entity.ProgressBar;
import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;

/**
 * 进度条控制层
 * 
 * v1.0 zhanghc 2020年10月13日下午5:39:55
 */
@Controller
@RequestMapping("/progressBar")
public class ProgressBarController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ProgressBarController.class);

	/**
	 * 获取进度条
	 * 
	 * v1.0 zhanghc 2020年10月13日下午5:39:59
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(String id) {
		try {
			ProgressBar progressBar = ProgressBarCache.getProgressBar(id);
			if (progressBar == null) {
				throw new MyException("参数错误：id");
			}
			return new PageResultEx(true, "查询成功", progressBar);
		} catch (MyException e) {
			log.error("获取进度条错误：", e);
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("获取进度条错误：", e);
			return new PageResult(false, "未知错误");
		}
	}
}
