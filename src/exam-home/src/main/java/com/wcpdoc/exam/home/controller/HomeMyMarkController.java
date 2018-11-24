package com.wcpdoc.exam.home.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.exam.service.ExamService;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 我的判卷控制层
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Controller
@RequestMapping("/home/myMark")
public class HomeMyMarkController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(HomeMyMarkController.class);
	
	@Resource
	private ExamService examService;
	
	/**
	 * 到达考试列表页面
	 * 
	 * v1.0 zhanghc 2017年6月28日上午7:34:23
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toExamList")
	public String toExamList(Model model) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			return "/WEB-INF/jsp/home/myMark/examList.jsp";
		} catch (Exception e) {
			log.error("到达考试列表页面错误：", e);
			return "/WEB-INF/jsp/home/myMark/examList.jsp";
		}
	}
	
	/**
	 * 考试列表
	 * 
	 * v1.0 zhanghc 2017年7月3日下午5:38:04
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/examList")
	@ResponseBody
	public PageOut examList(PageIn pageIn) {
		try {
			pageIn.setTen(getCurrentUser().getId().toString());
			pageIn.setFour("1");
			return examService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("考试列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达判卷列表页面
	 * 
	 * v1.0 zhanghc 2018年11月11日下午2:15:05
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toMarkList(Model model) {
		try {
			return "/WEB-INF/jsp/home/myMark/markList.jsp";
		} catch (Exception e) {
			log.error("到达判卷列表页面错误：", e);
			return "/WEB-INF/jsp/home/myMark/markList.jsp";
		}
	}
	
	/**
	 * 判卷列表
	 * 
	 * v1.0 zhanghc 2018年11月11日下午2:26:56
	 * @param pageIn
	 * @return String
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			pageIn.setTen(getCurrentUser().getId().toString());
			return examService.getMarkListpage(pageIn);
		} catch (Exception e) {
			log.error("判卷列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达判卷页面
	 * 
	 * v1.0 zhanghc 2017年7月4日下午1:54:03
	 * @param model
	 * @param examUserId
	 * @return String
	 */
	@RequestMapping("/toMark")
	public String toMark(Model model, Integer examUserId) {
		try {
			examService.toMark(model, getCurrentUser(), examUserId);
			return "/WEB-INF/jsp/home/myMark/markPaper.jsp";
		} catch (Exception e) {
			log.error("到达试卷页面错误：", e);
			model.addAttribute("message", e.getMessage());
			return "/WEB-INF/jsp/home/error.jsp";
		}
	}
	
	/**
	 * 更新判卷分数
	 * 
	 * v1.0 zhanghc 2017年7月4日下午5:45:56
	 * @param examUserQuestionId
	 * @param score
	 * @return PageResult
	 */
	@RequestMapping("/updateScore")
	@ResponseBody
	public PageResult updateScore(Integer examUserQuestionId, BigDecimal score) {
		try {
			examService.updateMarkScore(getCurrentUser(), examUserQuestionId, score);
			return new PageResult(true, "更新成功");
		} catch (Exception e) {
			log.error("更新判卷分数错误：", e);
			return new PageResult(false, "更新失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成判卷
	 * 
	 * v1.0 zhanghc 2017年7月4日下午9:52:46
	 * @param examUserId
	 * @return PageResult
	 */
	@RequestMapping("/doMark")
	@ResponseBody
	public PageResult doMark(Integer examUserId) {
		try {
			examService.doMark(getCurrentUser(), examUserId);
			return new PageResult(true, "判卷成功");
		} catch (Exception e) {
			log.error("完成判卷错误：", e);
			return new PageResult(false, "判卷失败：" + e.getMessage());
		}
	}
	
//	/**
//	 * 到达判卷预览页面
//	 * 
//	 * v1.0 zhanghc 2017-05-25 16:34:59
//	 * @return String
//	 */
//	@RequestMapping("/toMarkView")
//	public String toMarkView(Model model, Integer examUserId) {
//		try {
//			homeMarkService.toMarkView(getCurrentUser(), model, examUserId);
//			return "/WEB-INF/jsp/home/myMark/markPaperView.jsp";
//		} catch (Exception e) {
//			log.error("到达试卷预览页面错误：", e);
//			model.addAttribute("message", e.getMessage());
//			return "/WEB-INF/jsp/home/error.jsp";
//		}
//	}
}
