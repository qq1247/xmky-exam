package com.wcpdoc.exam.home.controller;

import java.util.ArrayList;
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
import com.wcpdoc.exam.core.entity.Exam;
import com.wcpdoc.exam.core.entity.ExamType;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.service.ExamService;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 考试控制层
 * 
 * v1.0 zhanghc 2018年10月25日下午9:23:06
 */
@Controller
@RequestMapping("/home/exam")
public class HomeExamController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(HomeExamController.class);
	
	@Resource
	private ExamService examService;
	
	/**
	 * 到达考试列表页面 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @param model
	 * @param nav
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model, boolean nav) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			model.addAttribute("nav", nav);
			return "/home/exam/examList";
		} catch (Exception e) {
			log.error("到达考试列表页面错误：", e);
			return "/home/exam/examList";
		}
	}
	
	/**
	 * 获取考试分类树
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/examTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> examTypeTreeList() {
		try {
			return examService.getExamTypeTreeList(getCurUser().getId());
		} catch (Exception e) {
			log.error("获取考试分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 到达添加试卷页面
	 * 
	 * v1.0 zhanghc 2018年10月27日上午9:17:39
	 * @param model
	 * @return PageResult
	 */
	@RequestMapping("/toPaperAdd")
	public String toPaperAdd(Model model) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			return "/home/exam/paperAdd";
		} catch (Exception e) {
			log.error("到达添加试卷页面错误：", e);
			return "/home/exam/paperAdd";
		}
	}
	
	/**
	 * 获取试卷分类树形列表
	 * 
	 * v1.0 zhanghc 2018年10月27日上午9:21:56
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/paperTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> getPaperTypeTreeList() {
		try {
			return examService.getPaperTypeTreeList(getCurUser().getId());
		} catch (Exception e) {
			log.error("获取试卷分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 试卷列表
	 * 
	 * v1.0 zhanghc 2018年10月27日上午9:22:15
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/paperList")
	@ResponseBody
	public PageOut paperList(PageIn pageIn) {
		try {
			if(getCurUser().getId() != 1){
				pageIn.setEight(getCurUser().getId() + "");
			}
			pageIn.setThree("1");
			return examService.getPaperListpage(pageIn);
		} catch (Exception e) {
			log.error("试卷列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 考试列表 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			if(getCurUser().getId() != 1){
				pageIn.setEight(getCurUser().getId().toString());
			}
			return examService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("考试列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达添加考试页面 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @param model
	 * @param examTypeId
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model, Integer examTypeId) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			model.addAttribute("examType", examService.getExamType2(examTypeId));
			return "/home/exam/examEdit";
		} catch (Exception e) {
			log.error("到达添加考试页面错误：", e);
			return "/home/exam/examEdit";
		}
	}
	
	/**
	 * 完成添加考试
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Exam exam) {
		try {
			examService.addAndUpdate(exam, getCurUser());
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加考试错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达修改考试页面 
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Exam exam = examService.getEntity(id);
			model.addAttribute("exam", exam);
			
			Paper paper = examService.getPaper(exam.getPaperId());
			model.addAttribute("paper", paper);
			
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			
			ExamType examType = examService.getExamType(id);
			model.addAttribute("examType", examType);
			return "/home/exam/examEdit";
		} catch (Exception e) {
			log.error("到达修改考试页面错误：", e);
			return "/home/exam/examEdit";
		}
	}
	
	/**
	 * 完成修改考试
	 * 
	 * v1.0 zhanghc 2018年10月25日下午9:23:06
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Exam exam) {
		try {
			examService.updateAndUpdate(exam, getCurUser());
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改考试错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成删除考试
	 * 
	 * v1.0 zhanghc 2017-06-11 09:13:23
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			examService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除考试错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成发布
	 * 
	 * v1.0 zhanghc 2018年11月24日上午9:13:22
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/doPublish")
	@ResponseBody
	public PageResult doPublish(Integer id) {
		try {
			examService.doPublish(id);
			return new PageResult(true, "发布成功");
		} catch (Exception e) {
			log.error("完成发布错误：", e);
			return new PageResult(false, "发布失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达考试用户列表页面
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toExamUserList")
	public String toExamUserList(Model model, Integer id) {
		try {
			model.addAttribute("id", id);
			return "/home/exam/examUserList";
		} catch (Exception e) {
			log.error("到达考试用户列表页面错误：", e);
			return "/home/exam/examUserList";
		}
	}
	
	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/examUserOrgTreeList")
	@ResponseBody
	public List<Map<String, Object>> examUserOrgTreeList() {
		try {
			return examService.getOrgTreeList();
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 考试用户列表 
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/examUserList")
	@ResponseBody
	public PageOut examUserList(PageIn pageIn) {
		try {
			return examService.getExamUserListpage(pageIn);
		} catch (Exception e) {
			log.error("考试用户列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达添加考试用户页面
	 * 
	 * v1.0 zhanghc 2017年6月19日上午7:37:14
	 * @param id
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toExamUserAdd")
	public String toExamUserAdd(Model model, Integer id) {
		try {
			model.addAttribute("id", id);
			return "/home/exam/examUserAdd";
		} catch (Exception e) {
			log.error("到达添加考试用户列表页面错误：", e);
			return "/home/exam/examUserAdd";
		}
	}
	
	/**
	 * 考试用户添加列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/examUserAddList")
	@ResponseBody
	public PageOut examUserAddList(PageIn pageIn) {
		try {
			return examService.getExamUserAddListpage(pageIn);
		} catch (Exception e) {
			log.error("考试用户添加列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 完成添加考试用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userids
	 * @return PageResult
	 */
	@RequestMapping("/doExamUserAdd")
	@ResponseBody
	public PageResult doExamUserAdd(Integer id, Integer[] userIds) {
		try {
			examService.doExamUserAdd(getCurUser(), id, userIds);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加考试用户错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成删除考试用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param examUserIds
	 * @return PageResult
	 */
	@RequestMapping("/doExamUserDel")
	@ResponseBody
	public PageResult doExamUserDel(Integer[] examUserIds) {
		try {
			examService.doExamUserDel(examUserIds);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除考试用户错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达判卷用户列表页面
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toMarkUserList")
	public String toMarkUserList(Model model, Integer id) {
		try {
			model.addAttribute("id", id);
			return "/home/exam/markUserList";
		} catch (Exception e) {
			log.error("到达判卷用户列表页面错误：", e);
			return "/home/exam/markUserList";
		}
	}
	
	/**
	 * 获取组织机构树
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/markUserOrgTreeList")
	@ResponseBody
	public List<Map<String, Object>> markUserOrgTreeList() {
		try {
			return examService.getOrgTreeList();
		} catch (Exception e) {
			log.error("获取组织机构树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 判卷用户列表 
	 * 
	 * v1.0 zhanghc 2018年10月31日上午10:27:22
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/markUserList")
	@ResponseBody
	public PageOut markUserList(PageIn pageIn) {
		try {
			return examService.getMarkUserListpage(pageIn);
		} catch (Exception e) {
			log.error("判卷用户列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 到达添加判卷用户页面
	 * 
	 * v1.0 zhanghc 2017年6月19日上午7:37:14
	 * @param id
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toMarkUserAdd")
	public String toMarkUserAdd(Model model, Integer id) {
		try {
			model.addAttribute("id", id);
			return "/home/exam/markUserAdd";
		} catch (Exception e) {
			log.error("到达添加判卷用户列表页面错误：", e);
			return "/home/exam/markUserAdd";
		}
	}
	
	/**
	 * 判卷用户添加列表 
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/markUserAddList")
	@ResponseBody
	public PageOut markUserAddList(PageIn pageIn) {
		try {
			return examService.getMarkUserAddListpage(pageIn);
		} catch (Exception e) {
			log.error("判卷用户添加列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 完成添加判卷用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param id
	 * @param userids
	 * @return PageResult
	 */
	@RequestMapping("/doMarkUserAdd")
	@ResponseBody
	public PageResult doMarkUserAdd(Integer id, Integer[] userIds) {
		try {
			examService.doMarkUserAdd(id, userIds);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加判卷用户错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成删除判卷用户
	 * 
	 * v1.0 zhanghc 2017年6月16日下午5:02:45
	 * @param markUserIds
	 * @return PageResult
	 */
	@RequestMapping("/doMarkUserDel")
	@ResponseBody
	public PageResult doMarkUserDel(Integer[] markUserIds) {
		try {
			examService.doMarkUserDel(markUserIds);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除判卷用户错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
}
