package com.wcpdoc.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.cache.ProgressBarCache;
import com.wcpdoc.base.entity.ProgressBar;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;

/**
 * 进度条控制层
 * 
 * v1.0 zhanghc 2020年10月13日下午5:39:55
 */
@Controller
@RequestMapping("/api/progressBar")
public class ApiProgressBarController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiProgressBarController.class);

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
			Map<String, Object> result = new HashMap<>();
			result.put("id", progressBar.getId());
			result.put("curNum", progressBar.getCurNum());
			result.put("totalNum", progressBar.getTotalNum());
			return PageResultEx.custom().data(result).code(progressBar.getCode()).msg(progressBar.getMsg());
		} catch (MyException e) {
			log.error("获取进度条错误：{}", e.getMessage());
			return PageResultEx.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取进度条错误：", e);
			return PageResultEx.err();
		}
	}
}
