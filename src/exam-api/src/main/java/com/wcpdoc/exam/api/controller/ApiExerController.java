package com.wcpdoc.exam.api.controller;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wcpdoc.base.util.CurLoginUserUtil;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.StringUtil;
import com.wcpdoc.exam.core.entity.Exer;
import com.wcpdoc.exam.core.service.ExerService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

import lombok.extern.slf4j.Slf4j;

/**
 * 练习控制层
 * 
 * v1.0 chenyun 2021-03-02 13:43:21
 */
@RestController
@RequestMapping("/api/exer")
@Slf4j
public class ApiExerController extends BaseController {
	
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
	public PageResult listpage(PageIn pageIn) {
		try {
			if (!CurLoginUserUtil.isAdmin()) {// 考试用户、阅卷用户没有权限；子管理员看自己；管理员看所有；
				pageIn.addParm("curUserId", getCurUser().getId());
			}
			PageOut pageOut = exerService.getListpage(pageIn);
			for (Map<String, Object> map : pageOut.getList()) {
				if (map.get("userIds") == null) {
					map.put("userIds", new Integer[0]);
				} else {
					map.put("userIds", StringUtil.toIntList(map.get("userIds").toString()));
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
	public PageResult del(Integer id) {
		try {
			// 数据校验
			Exer exer = exerService.getById(id);
			if (!(CurLoginUserUtil.isSelf(exer.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}
			
			// 删除
			exer.setState(0);
			exer.setUpdateTime(new Date());
			exer.setUpdateUserId(getCurUser().getId());
			exerService.updateById(exer);
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
	public PageResult get(Integer id) {
		try {
			Exer exer = exerService.getById(id);
			if (!(CurLoginUserUtil.isSelf(exer.getCreateUserId()) || CurLoginUserUtil.isAdmin())) {
				throw new MyException("无操作权限");
			}
			return PageResultEx.ok()
					.addAttr("id", exer.getId())
					.addAttr("name", exer.getName())
					.addAttr("questionTypeId", exer.getQuestionTypeId())
					.addAttr("questionTypeName", questionTypeService.getById(exer.getQuestionTypeId()).getName())
					.addAttr("startTime", exer.getStartTime())
					.addAttr("endTime", exer.getEndTime())
					.addAttr("userIds", exer.getUserIds())
					.addAttr("rmkState", exer.getRmkState())
					;
		} catch (MyException e) {
			log.error("练习获取错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("练习获取错误：", e);
			return PageResult.err();
		}
	}
}
