package com.wcpdoc.exam.exam.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.wcpdoc.exam.exam.entity.ExamType;
import com.wcpdoc.exam.exam.service.ExamTypeService;
import com.wcpdoc.exam.sys.entity.User;

/**
 * 考试分类控制层
 * 
 * v1.0 zhanghc 2017-06-28 21:34:41
 */
@Controller
@RequestMapping("/examType")
public class ExamTypeController extends BaseController{
	private static final Logger log = LoggerFactory.getLogger(ExamTypeController.class);
	
	@Resource
	private ExamTypeService examTypeService;
	
	/**
	 * 到达考试分类列表页面 
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "/WEB-INF/jsp/exam/examType/examTypeList.jsp";
		} catch (Exception e) {
			log.error("到达考试分类列表页面错误：", e);
			return "/WEB-INF/jsp/exam/examType/examTypeList.jsp";
		}
	}
	
	/**
	 * 获取考试分类树
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/treeList")
	@ResponseBody
	public List<Map<String, Object>> treeList() {
		try {
			return examTypeService.getTreeList();
		} catch (Exception e) {
			log.error("获取考试分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 考试分类列表 
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return examTypeService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("考试分类列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达添加考试分类页面 
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		try {
			return "/WEB-INF/jsp/exam/examType/examTypeEdit.jsp";
		} catch (Exception e) {
			log.error("到达添加考试分类页面错误", e);
			return "/WEB-INF/jsp/exam/examType/examTypeEdit.jsp";
		}
	}
	
	/**
	 * 完成添加考试分类
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(ExamType examType) {
		try {
			examType.setUpdateUserId(getCurrentUser().getId());
			examType.setUpdateTime(new Date());
			examType.setState(1);
			examTypeService.saveAndUpdate(examType);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加考试分类错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达修改考试分类页面 
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			ExamType examType = examTypeService.getEntity(id);
			model.addAttribute("examType", examType);
			
			ExamType pExamType = examTypeService.getEntity(examType.getParentId());
			model.addAttribute("pExamType", pExamType);
			return "/WEB-INF/jsp/exam/examType/examTypeEdit.jsp";
		} catch (Exception e) {
			log.error("到达修改考试分类页面错误", e);
			return "/WEB-INF/jsp/exam/examType/examTypeEdit.jsp";
		}
	}
	
	/**
	 * 完成修改考试分类
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(ExamType examType) {
		try {
			ExamType entity = examTypeService.getEntity(examType.getId());
			entity.setName(examType.getName());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(((User)getCurrentUser()).getId());
			entity.setNo(examType.getNo());
			examTypeService.editAndUpdate(entity);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改考试分类错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成删除考试分类
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			examTypeService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除考试分类错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达移动考试分类页面
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return String
	 */
	@RequestMapping("/toMove")
	public String toMove() {
		try {
			return "/WEB-INF/jsp/exam/examType/examTypeMove.jsp";
		} catch (Exception e) {
			log.error("到达移动考试分类页面错误", e);
			return "/WEB-INF/jsp/exam/examType/examTypeMove.jsp";
		}
	}
	
	/**
	 * 获取考试分类树
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/moveExamTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> moveExamTypeTreeList() {
		try {
			return examTypeService.getTreeList();
		} catch (Exception e) {
			log.error("获取考试分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 移动考试分类
	 * 
	 * v1.0 zhanghc 2017-06-28 21:34:41
	 * @param sourceId
	 * @param targetId
	 * @return PageResult
	 */
	@RequestMapping("/doMove")
	@ResponseBody
	public PageResult doMove(Integer sourceId, Integer targetId) {
		try {
			examTypeService.doMove(sourceId, targetId);
			return new PageResult(true, "移动成功");
		} catch (Exception e) {
			log.error("移动考试分类错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
		}
	}
}
