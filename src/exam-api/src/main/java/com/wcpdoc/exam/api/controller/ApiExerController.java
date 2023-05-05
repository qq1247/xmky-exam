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
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 练习控制层
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@Controller
@RequestMapping("/api/exer")
public class ApiExerController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiExerController.class);
	
	@Resource
	private ExerService exerService;
	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	
	/**
	 * 练习列表
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			PageOut pageOut = exerService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("userIds") == null) {
					map.put("userIds", new Integer[0]);
				} else {
					Exer exer = new Exer();
					exer.setUserIds(map.get("userIds").toString());
					map.put("userIds", exer.getUserIds());
				}
			}
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("练习列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 练习添加
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(Exer exer) {
		try {
			exerService.addEx(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习添加错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 练习修改
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(Exer exer) {
		try {
			exerService.updateEx(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习添加错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习添加错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 练习删除
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			Exer exer = exerService.getEntity(id);
			exer.setState(0);
			exer.setUpdateTime(new Date());
			exer.setUpdateUserId(getCurUser().getId());
			exerService.update(exer);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("练习删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习删除错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 练习获取
	 * 
	 * v1.0 chenyun 2021-03-02 13:43:21
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			Exer exer = exerService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", exer.getId())
					.addAttr("name", exer.getName())
					.addAttr("questionTypeId", exer.getQuestionTypeId())
					.addAttr("questionTypeName", questionTypeService.getEntity(exer.getQuestionTypeId()).getName())
					.addAttr("startTime", DateUtil.formatDateTime(exer.getStartTime()))
					.addAttr("endTime", DateUtil.formatDateTime(exer.getEndTime()))
					.addAttr("userIds", exer.getUserIds())
					.addAttr("rmkState", exer.getRmkState())
					;
		} catch (MyException e) {
			log.error("练习删除错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习删除错误：", e);
			return PageResult.err();
		}
	}
}
