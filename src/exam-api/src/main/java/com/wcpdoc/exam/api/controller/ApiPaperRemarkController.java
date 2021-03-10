package com.wcpdoc.exam.api.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
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
	@RequestMapping("/del")
	@ResponseBody
	public PageResult del(Integer id) {
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
