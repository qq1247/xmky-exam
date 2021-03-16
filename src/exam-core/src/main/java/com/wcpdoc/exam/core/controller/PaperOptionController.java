package com.wcpdoc.exam.core.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.PaperOption;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperOptionService;
/**
 * 试卷控制层
 * 
 * v1.0 chenyun 2021-03-10 13:47:35
 */
@Controller
@RequestMapping("/paperOption")
public class PaperOptionController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(PaperOptionController.class);
	
	@Resource
	private PaperOptionService paperOptionService;
	
	/**
	 * 到达试卷列表页面
	 * 
	 * v1.0 chenyun 2021-03-10 13:47:35
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "core/paperOption/paperOptionList";
		} catch (Exception e) {
			log.error("到达试卷列表页面错误：", e);
			return "core/paperOption/paperOptionList";
		}
	}
	
	/**
	 * 试卷列表
	 * 
	 * v1.0 chenyun 2021-03-10 13:47:35
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(paperOptionService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试卷列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达添加试卷页面
	 * 
	 * v1.0 chenyun 2021-03-10 13:47:35
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			return "core/paperOption/paperOptionEdit";
		} catch (Exception e) {
			log.error("到达添加试卷页面错误：", e);
			return "core/paperOption/paperOptionEdit";
		}
	}
	
	/**
	 * 完成添加试卷
	 * 
	 * v1.0 chenyun 2021-03-10 13:47:35
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(PaperOption paperOption) {
		try {
			paperOptionService.add(paperOption);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成添加试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达修改试卷页面
	 * 
	 * v1.0 chenyun 2021-03-10 13:47:35
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			PaperOption paperOption = paperOptionService.getEntity(id);
			model.addAttribute("paperOption", paperOption);
			return "core/paperOption/paperOptionEdit";
		} catch (Exception e) {
			log.error("到达修改试卷页面错误：", e);
			return "core/paperOption/paperOptionEdit";
		}
	}
	
	/**
	 * 完成修改试卷
	 * 
	 * v1.0 chenyun 2021-03-10 13:47:35
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(PaperOption paperOption) {
		try {
			PaperOption entity = paperOptionService.getEntity(paperOption.getId());
			entity.setQuestion(paperOption.getQuestion());
			entity.setQuestionOption(paperOption.getQuestionOption());
			entity.setRightClick(paperOption.getRightClick());
			entity.setRightCopy(paperOption.getRightCopy());
			entity.setMinimize(paperOption.getMinimize());
			entity.setMinimizeNum(paperOption.getMinimizeNum());
			entity.setPaperId(paperOption.getPaperId());
			paperOptionService.update(entity);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成修改试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试卷错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 完成删除试卷
	 * 
	 * v1.0 chenyun 2021-03-10 13:47:35
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			paperOptionService.delAndUpdate(id);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除试卷错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试卷错误：", e);
			return PageResult.err();
		}
	}

}
