package com.wcpdoc.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.cache.ProgressBarCache;
import com.wcpdoc.base.entity.ProgressBar;
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

	/**
	 * 获取进度条
	 * 
	 * v1.0 zhanghc 2020年10月13日下午5:39:59
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/get")
	public PageResult get(String id) {
		try {
			ProgressBar progressBar = ProgressBarCache.getProgressBar(id);
			if (progressBar == null) {
				throw new MyException(String.format("参数错误：id=%s", id));
			}
			Map<String, Object> result = new HashMap<>();
			result.put("curNum", progressBar.getCurNum());
			result.put("totalNum", progressBar.getTotalNum());
			result.put("percent", progressBar.getPercent());
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
