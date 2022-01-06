package com.wcpdoc.exam.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageIn;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.service.PaperRemarkService;
/**
 * 试卷评语控制层
 * 
 * v1.0 chenyun 2021-03-10 13:48:34
 */
@Controller
@RequestMapping("/api/paperRemark")
public class ApiPaperRemarkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiPaperRemarkController.class);
	
	@Resource
	private PaperRemarkService paperRemarkService;
	
	/**
	 * 试卷评语列表
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	public PageResult listpage(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(paperRemarkService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试卷评语列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成添加试卷评语
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return pageOut
	 */
	@RequestMapping("/add")
	@ResponseBody
	public PageResult add(PaperRemark paperRemark) {
		try {
			paperRemarkService.add(paperRemark);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加试卷评语错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试卷评语错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成修改试卷评语
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return pageOut
	 */
	@RequestMapping("/edit")
	@ResponseBody
	public PageResult edit(PaperRemark paperRemark) {
		try {
			PaperRemark entity = paperRemarkService.getEntity(paperRemark.getId());
			entity.setNo(paperRemark.getNo());
			entity.setScore(paperRemark.getScore());
			entity.setRemark(paperRemark.getRemark());
			paperRemarkService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改试卷评语错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试卷评语错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成删除试卷评语
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return pageOut
	 */
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
		try {
			paperRemarkService.del(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除试卷评语错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试卷评语错误：", e);
			return PageResult.err();
		}
	}
}
