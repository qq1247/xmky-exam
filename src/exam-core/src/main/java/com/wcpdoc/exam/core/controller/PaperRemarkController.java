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
import com.wcpdoc.exam.core.entity.PaperRemark;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperRemarkService;
/**
 * 试卷评语控制层
 * 
 * v1.0 chenyun 2021-03-10 13:48:34
 */
@Controller
@RequestMapping("/paperRemark")
public class PaperRemarkController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(PaperRemarkController.class);
	
	@Resource
	private PaperRemarkService paperRemarkService;
	
	/**
	 * 到达试卷评语列表页面
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "core/paperRemark/paperRemarkList";
		} catch (Exception e) {
			log.error("到达试卷评语列表页面错误：", e);
			return "core/paperRemark/paperRemarkList";
		}
	}
	
	/**
	 * 试卷评语列表
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			return PageResultEx.ok().data(paperRemarkService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试卷评语列表错误：", e);
			return PageResult.err();
		}
	}
	
	/**
	 * 到达添加试卷评语页面
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			return "core/paperRemark/paperRemarkEdit";
		} catch (Exception e) {
			log.error("到达添加试卷评语页面错误：", e);
			return "core/paperRemark/paperRemarkEdit";
		}
	}
	
	/**
	 * 完成添加试卷评语
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(PaperRemark paperRemark) {
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
	 * 到达修改试卷评语页面
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			PaperRemark paperRemark = paperRemarkService.getEntity(id);
			model.addAttribute("paperRemark", paperRemark);
			return "core/paperRemark/paperRemarkEdit";
		} catch (Exception e) {
			log.error("到达修改试卷评语页面错误：", e);
			return "core/paperRemark/paperRemarkEdit";
		}
	}
	
	/**
	 * 完成修改试卷评语
	 * 
	 * v1.0 chenyun 2021-03-10 13:48:34
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(PaperRemark paperRemark) {
		try {
			PaperRemark entity = paperRemarkService.getEntity(paperRemark.getId());
			entity.setScoreA(paperRemark.getScoreA());
			entity.setScoreARemark(paperRemark.getScoreARemark());
			entity.setScoreB(paperRemark.getScoreB());
			entity.setScoreBRemark(paperRemark.getScoreBRemark());
			entity.setScoreC(paperRemark.getScoreC());
			entity.setScoreCRemark(paperRemark.getScoreCRemark());
			entity.setScoreD(paperRemark.getScoreD());
			entity.setScoreDRemark(paperRemark.getScoreDRemark());
			entity.setScoreE(paperRemark.getScoreE());
			entity.setScoreERemark(paperRemark.getScoreERemark());
			entity.setPaperId(paperRemark.getPaperId());
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
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			paperRemarkService.delAndUpdate(id);
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
