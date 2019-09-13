package com.wcpdoc.exam.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wcpdoc.exam.core.controller.BaseController;

/**
 * 考试控制层
 * 
 * v1.0 zhanghc 2017-06-11 09:13:23
 */
@Controller
@RequestMapping("/exam")
@Deprecated
public class ExamController extends BaseController{
//	private static final Logger log = LoggerFactory.getLogger(ExamController.class);
//	
//	@Resource
//	private ExamService examService;
//	
//	/**
//	 * 到达考试列表页面 
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return String
//	 */
//	@RequestMapping("/toList")
//	public String toList() {
//		try {
//			return "/exam/exam/examList";
//		} catch (Exception e) {
//			log.error("到达考试列表页面错误：", e);
//			return "/exam/exam/examList";
//		}
//	}
//	
//	/**
//	 * 获取考试分类树
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return List<Map<String,Object>>
//	 */
//	@RequestMapping("/examTypeTreeList")
//	@ResponseBody
//	public List<Map<String, Object>> examTypeTreeList() {
//		try {
//			return examService.getExamTypeTreeList();
//		} catch (Exception e) {
//			log.error("获取考试分类树错误：", e);
//			return new ArrayList<Map<String,Object>>();
//		}
//	}
//	
//	/**
//	 * 考试列表 
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return pageOut
//	 */
//	@RequestMapping("/list")
//	@ResponseBody
//	public PageOut list(PageIn pageIn) {
//		try {
//			return examService.getListpage(pageIn);
//		} catch (Exception e) {
//			log.error("考试列表错误：", e);
//			return new PageOut();
//		}
//	}
//	
//	/**
//	 * 到达添加考试页面 
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return String
//	 */
//	@RequestMapping("/toAdd")
//	public String toAdd(Model model) {
//		try {
//			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
//			return "/exam/exam/examEdit";
//		} catch (Exception e) {
//			log.error("到达添加考试页面错误：", e);
//			return "/exam/exam/examEdit";
//		}
//	}
//	
//	/**
//	 * 到达添加试卷页面
//	 * 
//	 * v1.0 zhanghc 2017年6月11日下午5:47:06
//	 * @param id
//	 * @return String
//	 */
//	@RequestMapping("/toPaperAddList")
//	public String toPaperAddList(Integer id) {
//		try {
//			return "/exam/exam/examPaperAddList";
//		} catch (Exception e) {
//			log.error("到达添加试卷页面错误：", e);
//			return "/exam/exam/examPaperAddList";
//		}
//	}
//	
//	/**
//	 * 获取试卷分类树形列表
//	 * 
//	 * v1.0 zhanghc 2017年6月11日下午5:48:37
//	 * @return List<Map<String,Object>>
//	 */
//	@RequestMapping("/paperAddListTypeTreeList")
//	@ResponseBody
//	public List<Map<String, Object>> paperAddListTypeTreeList() {
//		try {
//			return examService.getPaperAddListTypeTreeList();
//		} catch (Exception e) {
//			log.error("获取试卷分类树错误：", e);
//			return new ArrayList<Map<String,Object>>();
//		}
//	}
//	
//	/**
//	 * 获取试卷列表 
//	 * 
//	 * v1.0 zhanghc 2017年6月11日下午5:50:36
//	 * @param pageIn
//	 * @return PageOut
//	 */
//	@RequestMapping("/paperAddList")
//	@ResponseBody
//	public PageOut paperAddList(PageIn pageIn) {
//		try {
//			return examService.getPaperListpage(pageIn);
//		} catch (Exception e) {
//			log.error("试卷列表错误：", e);
//			return new PageOut();
//		}
//	}
//	
//	/**
//	 * 完成添加考试
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return pageOut
//	 */
//	@RequestMapping("/doAdd")
//	@ResponseBody
//	public PageResult doAdd(Exam exam) {
//		try {
//			exam.setUpdateTime(new Date());
//			exam.setUpdateUserId(getCurUser().getId());
//			examService.addAndUpdate(exam);
//			return new PageResult(true, "添加成功");
//		} catch (Exception e) {
//			log.error("完成添加考试错误：", e);
//			return new PageResult(false, "添加失败：" + e.getMessage());
//		}
//	}
//	
//	/**
//	 * 到达修改考试页面 
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return String
//	 */
//	@RequestMapping("/toEdit")
//	public String toEdit(Model model, Integer id) {
//		try {
//			Exam exam = examService.getEntity(id);
//			model.addAttribute("exam", exam);
//			Paper paper = examService.getPaper(exam.getPaperId());
//			model.addAttribute("paper", paper);
//			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
//			
//			ExamType examType = examService.getExamType(id);
//			model.addAttribute("examType", examType);
//			return "/exam/exam/examEdit";
//		} catch (Exception e) {
//			log.error("到达修改考试页面错误：", e);
//			return "/exam/exam/examEdit";
//		}
//	}
//	
//	/**
//	 * 完成修改考试
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return pageOut
//	 */
//	@RequestMapping("/doEdit")
//	@ResponseBody
//	public PageResult doEdit(Exam exam) {
//		try {
//			Exam entity = examService.getEntity(exam.getId());
//			entity.setUpdateTime(new Date());
//			entity.setUpdateUserId(getCurUser().getId());
//			entity.setName(exam.getName());
//			entity.setPassScore(exam.getPassScore());
//			entity.setState(exam.getState());
//			entity.setStartTime(exam.getStartTime());
//			entity.setEndTime(exam.getEndTime());
//			entity.setDescription(exam.getDescription());
//			entity.setPaperId(exam.getPaperId());
//			examService.updateAndUpdate(entity);
//			return new PageResult(true, "修改成功");
//		} catch (Exception e) {
//			log.error("完成修改考试错误：", e);
//			return new PageResult(false, "修改失败：" + e.getMessage());
//		}
//	}
//	
//	/**
//	 * 完成删除考试
//	 * 
//	 * v1.0 zhanghc 2017-06-11 09:13:23
//	 * @return pageOut
//	 */
//	@RequestMapping("/doDel")
//	@ResponseBody
//	public PageResult doDel(Integer[] ids) {
//		try {
//			examService.delAndUpdate(ids);
//			return new PageResult(true, "删除成功");
//		} catch (Exception e) {
//			log.error("完成删除考试错误：", e);
//			return new PageResult(false, "删除失败：" + e.getMessage());
//		}
//	}
//	
//	/**
//	 * 到达考试用户列表页面
//	 * 
//	 * v1.0 zhanghc 2017年6月12日下午11:33:39
//	 * @param id
//	 * @return String
//	 */
//	@RequestMapping("/toExamUserList")
//	public String toExamUserList(Model model, Integer id) {
//		try {
//			model.addAttribute("id", id);
//			return "/exam/exam/examUserList";
//		} catch (Exception e) {
//			log.error("到达考试用户列表页面错误：", e);
//			return "/exam/exam/examUserList";
//		}
//	}
//	
//	/**
//	 * 获取组织机构树
//	 * 
//	 * v1.0 zhanghc 2017年6月12日下午11:33:39
//	 * @return List<Map<String,Object>>
//	 */
//	@RequestMapping("/examUserOrgTreeList")
//	@ResponseBody
//	public List<Map<String, Object>> examUserOrgTreeList() {
//		try {
//			return examService.getOrgTreeList();
//		} catch (Exception e) {
//			log.error("获取组织机构树错误：", e);
//			return new ArrayList<Map<String,Object>>();
//		}
//	}
//	
//	/**
//	 * 考试用户列表 
//	 * 
//	 * v1.0 zhanghc 2017年6月16日下午5:02:45
//	 * @param pageIn
//	 * @return PageOut
//	 */
//	@RequestMapping("/examUserList")
//	@ResponseBody
//	public PageOut examUserList(PageIn pageIn) {
//		try {
//			return examService.getExamUserListpage(pageIn);
//		} catch (Exception e) {
//			log.error("考试用户列表错误：", e);
//			return new PageOut();
//		}
//	}
//	
//	/**
//	 * 到达添加考试用户列表页面
//	 * 
//	 * v1.0 zhanghc 2017年6月19日上午7:37:14
//	 * @param id
//	 * @param model
//	 * @return String
//	 */
//	@RequestMapping("/toExamUserAddList")
//	public String toExamUserAddList(Model model, Integer id) {
//		try {
//			model.addAttribute("id", id);
//			return "/exam/exam/examUserAddList";
//		} catch (Exception e) {
//			log.error("到达添加考试用户列表页面错误：", e);
//			return "/exam/exam/examUserAddList";
//		}
//	}
//	
//	/**
//	 * 获取组织机构树
//	 * v1.0 zhanghc 2017年6月12日下午11:33:39
//	 * 
//	 * @return List<Map<String,Object>>
//	 */
//	@RequestMapping("/examUserAddOrgTreeList")
//	@ResponseBody
//	public List<Map<String, Object>> examUserAddOrgTreeList() {
//		try {
//			return examService.getExamUserAddOrgTreeList();
//		} catch (Exception e) {
//			log.error("获取组织机构树错误：", e);
//			return new ArrayList<Map<String,Object>>();
//		}
//	}
//	
//	/**
//	 * 考试用户添加列表 
//	 * 
//	 * v1.0 zhanghc 2017年6月16日下午5:02:45
//	 * @param pageIn
//	 * @return PageOut
//	 */
//	@RequestMapping("/examUserAddList")
//	@ResponseBody
//	public PageOut examUserAddList(PageIn pageIn) {
//		try {
//			return examService.getExamUserAddListpage(pageIn);
//		} catch (Exception e) {
//			log.error("考试用户添加列表错误：", e);
//			return new PageOut();
//		}
//	}
//	
//	/**
//	 * 完成添加考试用户
//	 * 
//	 * v1.0 zhanghc 2017年6月19日下午3:09:23
//	 * @param id
//	 * @param userids
//	 * @return PageResult
//	 */
//	@RequestMapping("/doExamUserAdd")
//	@ResponseBody
//	public PageResult doExamUserAdd(Integer id, Integer[] userIds) {
//		try {
//			examService.doExamUserAdd(getCurUser(), id, userIds);
//			return new PageResult(true, "添加成功");
//		} catch (Exception e) {
//			log.error("完成添加考试用户错误：", e);
//			return new PageResult(false, "添加失败：" + e.getMessage());
//		}
//	}
//	
//	/**
//	 * 完成删除考试用户
//	 * 
//	 * v1.0 zhanghc 2017年6月19日下午3:09:23
//	 * @param examUserIds
//	 * @return PageResult
//	 */
//	@RequestMapping("/doExamUserDel")
//	@ResponseBody
//	public PageResult doExamUserDel(Integer[] examUserIds) {
//		try {
//			examService.doExamUserDel(examUserIds);
//			return new PageResult(true, "删除成功");
//		} catch (Exception e) {
//			log.error("完成删除考试用户错误：", e);
//			return new PageResult(false, "删除失败：" + e.getMessage());
//		}
//	}
//	
//	/**
//	 * 到达判卷用户列表页面
//	 * 
//	 * v1.0 zhanghc 2017年6月12日下午11:33:39
//	 * @param id
//	 * @return String
//	 */
//	@RequestMapping("/toMarkUserList")
//	public String toMarkUserList(Model model, Integer id) {
//		try {
//			model.addAttribute("id", id);
//			return "/exam/exam/examMarkUserList";
//		} catch (Exception e) {
//			log.error("到达判卷用户列表页面错误：", e);
//			return "/exam/exam/examMarkUserList";
//		}
//	}
//	
//	/**
//	 * 获取组织机构树
//	 * 
//	 * v1.0 zhanghc 2017年6月12日下午11:33:39
//	 * @return List<Map<String,Object>>
//	 */
//	@RequestMapping("/markUserOrgTreeList")
//	@ResponseBody
//	public List<Map<String, Object>> markUserOrgTreeList() {
//		try {
//			return examService.getOrgTreeList();
//		} catch (Exception e) {
//			log.error("获取组织机构树错误：", e);
//			return new ArrayList<Map<String,Object>>();
//		}
//	}
//	
//	/**
//	 * 判卷用户列表 
//	 * 
//	 * v1.0 zhanghc 2017年6月16日下午5:02:45
//	 * @param pageIn
//	 * @return PageOut
//	 */
//	@RequestMapping("/markUserList")
//	@ResponseBody
//	public PageOut markUserList(PageIn pageIn) {
//		try {
//			return examService.getMarkUserListpage(pageIn);
//		} catch (Exception e) {
//			log.error("判卷用户列表错误：", e);
//			return new PageOut();
//		}
//	}
//	
//	/**
//	 * 到达添加判卷用户列表页面
//	 * 
//	 * v1.0 zhanghc 2017年6月19日上午7:37:14
//	 * @param id
//	 * @param model
//	 * @return String
//	 */
//	@RequestMapping("/toMarkUserAddList")
//	public String toMarkUserAddList(Model model, Integer id) {
//		try {
//			model.addAttribute("id", id);
//			return "/exam/exam/examMarkUserAddList";
//		} catch (Exception e) {
//			log.error("到达添加判卷用户列表页面错误：", e);
//			return "/exam/exam/examMarkUserAddList";
//		}
//	}
//	
//	/**
//	 * 获取组织机构树
//	 * 
//	 * v1.0 zhanghc 2017年6月12日下午11:33:39
//	 * @return List<Map<String,Object>>
//	 */
//	@RequestMapping("/markUserAddOrgTreeList")
//	@ResponseBody
//	public List<Map<String, Object>> markUserAddOrgTreeList() {
//		try {
//			return examService.getOrgTreeList();
//		} catch (Exception e) {
//			log.error("获取组织机构树错误：", e);
//			return new ArrayList<Map<String,Object>>();
//		}
//	}
//	
//	/**
//	 * 判卷用户添加列表 
//	 * 
//	 * v1.0 zhanghc 2017年6月16日下午5:02:45
//	 * @param pageIn
//	 * @return PageOut
//	 */
//	@RequestMapping("/markUserAddList")
//	@ResponseBody
//	public PageOut markUserAddList(PageIn pageIn) {
//		try {
//			return examService.getMarkUserAddListpage(pageIn);
//		} catch (Exception e) {
//			log.error("判卷用户添加列表错误：", e);
//			return new PageOut();
//		}
//	}
//	
//	/**
//	 * 完成添加判卷用户
//	 * 
//	 * v1.0 zhanghc 2017年6月19日下午3:09:23
//	 * @param id
//	 * @param userids
//	 * @return PageResult
//	 */
//	@RequestMapping("/doMarkUserAdd")
//	@ResponseBody
//	public PageResult doMarkUserAdd(Integer id, Integer[] userIds) {
//		try {
//			examService.doMarkUserAdd(id, userIds);
//			return new PageResult(true, "添加成功");
//		} catch (Exception e) {
//			log.error("完成添加判卷用户错误：", e);
//			return new PageResult(false, "添加失败：" + e.getMessage());
//		}
//	}
//	
//	/**
//	 * 完成删除判卷用户
//	 * 
//	 * v1.0 zhanghc 2017年6月19日下午3:09:23
//	 * @param markUserIds
//	 * @return PageResult
//	 */
//	@RequestMapping("/doMarkUserDel")
//	@ResponseBody
//	public PageResult doMarkUserDel(Integer[] markUserIds) {
//		try {
//			examService.doMarkUserDel(markUserIds);
//			return new PageResult(true, "删除成功");
//		} catch (Exception e) {
//			log.error("完成删除判卷用户错误：", e);
//			return new PageResult(false, "删除失败：" + e.getMessage());
//		}
//	}
}
