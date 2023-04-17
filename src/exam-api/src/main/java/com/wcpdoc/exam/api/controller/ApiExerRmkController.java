package com.wcpdoc.exam.api.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.ExerRmk;
import com.wcpdoc.exam.core.service.ExerRmkService;

/**
 * 模拟练习评论控制层
 * 
 * v1.0 chenyun 2021年8月31日上午9:54:28
 */
@Controller
@RequestMapping("/api/exerRmk")
public class ApiExerRmkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiExerRmkController.class);

	@Resource
	private ExerRmkService exerRmkService;

	/**
	 * 模拟练习评论列表
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * 
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageOut listpage = exerRmkService.getListpage(new PageIn(request));
			for (Map<String, Object> mapList : listpage.getList()) {// 敏感词过滤
				mapList.put("content", mapList.get("content").toString());
			}
			return PageResultEx.ok().data(listpage);
		} catch (Exception e) {
			log.error("模拟练习评论列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 添加模拟练习评论
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * 
	 * @param exerRmk
	 * @return PageResult
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(ExerRmk exerRmk, Integer anonymity) {
		try {
			exerRmkService.addEx(exerRmk, anonymity);
			Map<String, Object> data = new HashMap<String, Object>();
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("添加模拟练习评论错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加模拟练习评论错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 删除模拟练习评论
	 * 
	 * v1.0 chenyun 2021年8月31日上午9:54:28
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			// exerRmkService.delEx(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除模拟练习评论错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除模拟练习评论错误：", e);
			return PageResult.err();
		}
	}
}
