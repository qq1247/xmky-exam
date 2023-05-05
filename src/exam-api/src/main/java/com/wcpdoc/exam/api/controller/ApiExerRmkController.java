package com.wcpdoc.exam.api.controller;

import java.util.Date;
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
import com.wcpdoc.core.util.ValidateUtil;
import com.wcpdoc.exam.core.entity.ExerRmk;
import com.wcpdoc.exam.core.service.ExerRmkService;

/**
 * 练习评论控制层
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
	 * 练习评论列表
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
			PageOut pageOut = exerRmkService.getListpage(new PageIn(request));
			for (Map<String, Object> map : pageOut.getList()) {
				if (!ValidateUtil.isValid(map.get("likeUserIds").toString())) {
					map.put("likeUserIds", new Integer[0]);
					continue;
				}
				
				String likeUserIds = map.get("likeUserIds").toString();
				String[] likeUserIdStrArr = likeUserIds.substring(1, likeUserIds.length() - 1).split(",");
				Integer[] likeUserIdArr = new Integer[likeUserIdStrArr.length];
				for (int i = 0; i < likeUserIdStrArr.length; i++) {
					likeUserIdArr[i] = Integer.parseInt(likeUserIdStrArr[i]);
				}
				map.put("likeUserIds", likeUserIdArr);
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("练习评论列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 练习评论删除
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
			// 练习评论删除
			ExerRmk exerRmk = exerRmkService.getEntity(id);
			exerRmk.setState(0);
			exerRmk.setUpdateTime(new Date());
			exerRmk.setUpdateUserId(getCurUser().getId());
			exerRmkService.update(exerRmk);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习评论删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习评论删除错误：", e);
			return PageResult.err();
		}
	}
}
