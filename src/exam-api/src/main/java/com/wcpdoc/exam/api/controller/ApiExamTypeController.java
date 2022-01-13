package com.wcpdoc.exam.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.base.service.UserService;
import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageOut;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.service.ExamTypeService;

/**
 * 考试分类控制层
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Controller
@RequestMapping("/api/examType")
public class ApiExamTypeController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiExamTypeController.class);
	
	@Resource
	private ExamTypeService examTypeService;
	@Resource
	private UserService userService;
	
	/**
	 * 考试分类列表 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage() {
		try {
			PageIn pageIn = new PageIn(request);
			pageIn.addAttr("curUserId", getCurUser().getId());
			PageOut pageOut = examTypeService.getListpage(pageIn);
			return PageResultEx.ok().data(pageOut);
		} catch (Exception e) {
			log.error("考试分类列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 添加考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(ExamType examType) {
		try {
			examTypeService.addAndUpdate(examType);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("添加考试分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("添加考试分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 修改考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(ExamType examType) {
		try {
			examTypeService.editAndUpdate(examType);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("修改考试分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("修改考试分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 删除考试分类
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			examTypeService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("删除考试分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("删除考试分类错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 获取考试分类
	 * 
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * @return pageOut
	 */
	@RequestMapping("/get")
	@ResponseBody
	public PageResult get(Integer id) {
		try {
			ExamType entity = examTypeService.getEntity(id);
			return PageResultEx.ok()
					.addAttr("id", entity.getId())
					.addAttr("name", entity.getName());
		} catch (MyException e) {
			log.error("获取考试分类错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("获取考试分类错误：", e);
			return PageResult.err();
		}
	}
}
