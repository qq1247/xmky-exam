package com.wcpdoc.exam.exam.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wcpdoc.exam.core.controller.BaseController;

/**
 * 试卷控制层
 * 
 * v1.0 zhanghc 2017-05-25 16:34:59
 */
@Controller
@RequestMapping("/paper")
@Deprecated
public class PaperController extends BaseController{
	/*private static final Logger log = LoggerFactory.getLogger(PaperController.class);
	
	@Resource
	private PaperService paperService;
	
	*//**
	 * 到达试卷列表页面 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 *//*
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "/WEB-INF/jsp/exam/paper/paperList.jsp";
		} catch (Exception e) {
			log.error("到达试卷列表页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperList.jsp";
		}
	}
	
	*//**
	 * 获取试卷分类数据
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 *//*
	@RequestMapping("/paperTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> getPaperTypeTreeList() {
		try {
			return paperService.getPaperTypeTreeList();
		} catch (Exception e) {
			log.error("获取试卷分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	*//**
	 * 试卷列表 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 *//*
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return paperService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("试卷列表错误：", e);
			return new PageOut();
		}
	}
	
	*//**
	 * 到达添加试卷页面 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 *//*
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
			return "/WEB-INF/jsp/exam/paper/paperEdit.jsp";
		} catch (Exception e) {
			log.error("到达添加试卷页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperEdit.jsp";
		}
	}
	
	*//**
	 * 完成添加试卷
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 *//*
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Paper paper) {
		try {
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurrentUser().getId());
			paperService.save(paper);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加试卷错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达修改试卷页面 
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 *//*
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			model.addAttribute("paper", paper);
			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
			
			PaperType paperType = paperService.getPaperType(id);
			model.addAttribute("paperType", paperType);
			return "/WEB-INF/jsp/exam/paper/paperEdit.jsp";
		} catch (Exception e) {
			log.error("到达修改试卷页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperEdit.jsp";
		}
	}
	
	*//**
	 * 完成修改试卷
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 *//*
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Paper paper) {
		try {
			Paper entity = paperService.getEntity(paper.getId());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurrentUser().getId());
			entity.setName(paper.getName());
			entity.setDescription(paper.getDescription());
			entity.setState(paper.getState());
			entity.setPaperTypeId(paper.getPaperTypeId());
			paperService.update(entity);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改试卷错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 完成删除试卷
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return pageOut
	 *//*
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			paperService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除试卷错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达设置试卷分类页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 *//*
	@RequestMapping("/toPaperTypeUpdate")
	public String toPaperTypeUpdate() {
		try {
			return "/WEB-INF/jsp/exam/paper/paperPaperTypeUpdate.jsp";
		} catch (Exception e) {
			log.error("到达设置试卷分类页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperPaperTypeUpdate.jsp";
		}
	}
	
	*//**
	 * 获取试卷分类树
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 *//*
	@RequestMapping("/paperTypeUpdatePaperTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> paperTypeUpdatePaperTypeTreeList() {
		try {
			return paperService.getPaperTypeTreeList();
		} catch (Exception e) {
			log.error("获取试卷分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	*//**
	 * 完成设置试卷分类
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @param ids 用户ID
	 * @param paperTypeId 试卷分类ID
	 * @return PageResult
	 *//*
	@RequestMapping("/doPaperTypeUpdate")
	@ResponseBody
	public PageResult doPaperTypeUpdate(Integer[] ids, Integer paperTypeId) {
		try {
			paperService.doPaperTypeUpdate(ids, paperTypeId);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置试卷分类错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达配置试卷页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return String
	 *//*
	@RequestMapping("/toPaperCfg")
	public String toPaperCfg(Model model, Integer id) {
		try {
			paperService.initRootPaperQuestion(id, getCurrentUser());
			model.addAttribute("id", id);
			return "/WEB-INF/jsp/exam/paper/paperCfg.jsp";
		} catch (Exception e) {
			log.error("到达配置试卷页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperCfg.jsp";
		}
	}
	
	*//**
	 * 获取配置试卷树
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * @return List<Map<String,Object>>
	 *//*
	@RequestMapping("/paperCfgPaperTreeList")
	@ResponseBody
	public List<Map<String, Object>> paperCfgPaperTreeList(Integer id) {
		try {
			return paperService.getPaperCfgPaperTreeList(id);
		} catch (Exception e) {
			log.error("获取配置试卷树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	*//**
	 * 
	 * 到达配置试卷添加章节页面 
	 * 
	 * v1.0 zhanghc 2017年5月26日下午4:41:34
	 * @return  String
	 *//*
	@RequestMapping("/toPaperCfgAdd")
	public String toPaperCfgAdd() {
		try {
			return "/WEB-INF/jsp/exam/paper/paperCfgEdit.jsp";
		} catch (Exception e) {
			log.error("到达配置试卷添加章节页面 错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperCfgEdit.jsp";
		}
	}
	
	*//**
	 * 完成配置试卷添加章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午9:36:57
	 * @param paper
	 * @return PageResult
	 *//*
	@RequestMapping("/doPaperCfgAdd")
	@ResponseBody
	public PageResult doPaperCfgAdd(PaperQuestion paperQuestion) {
		try {
			paperService.doPaperCfgAdd(paperQuestion, getCurrentUser());
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成配置试卷添加章节错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达配置试卷修改章节页面 
	 * 
	 * v1.0 zhanghc 2017年5月27日上午10:55:21
	 * @param paperQuestionId
	 * @param model
	 * @return String
	 *//*
	@RequestMapping("/toPaperCfgEdit")
	public String toPaperCfgEdit(Model model, Integer paperQuestionId) {
		try {
			PaperQuestion paperQuestion = paperService.getPaperQuestion(paperQuestionId);
			model.addAttribute("paperQuestion", paperQuestion);
			return "/WEB-INF/jsp/exam/paper/paperCfgEdit.jsp";
		} catch (Exception e) {
			log.error("到达配置试卷修改章节页面 错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperCfgEdit.jsp";
		}
	}
	
	*//**
	 * 完成配置试卷修改章节
	 * 
	 * v1.0 zhanghc 2017年5月27日上午11:03:59
	 * @param paperQuestion
	 * @return PageResult
	 *//*
	@RequestMapping("/doPaperCfgEdit")
	@ResponseBody
	public PageResult doPaperCfgEdit(PaperQuestion paperQuestion) {
		try {
			paperService.doPaperCfgEdit(paperQuestion, getCurrentUser());
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成配置试卷修改章节错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达配置试卷试题列表页面
	 * 
	 * v1.0 zhanghc 2017年5月27日下午2:37:00
	 * @return String
	 *//*
	@RequestMapping("/toPaperCfgList")
	public String toPaperCfgList(Model model) {
		try {
			model.addAttribute("QUESTION_DIFFICULTY", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("QUESTION_TYPE", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			return "/WEB-INF/jsp/exam/paper/paperCfgList.jsp";
		} catch (Exception e) {
			log.error("到达配置试卷试题列表页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperCfgList.jsp";
		}
	}
	
	*//**
	 * 获取配置试卷试题分类树
	 * 
	 * v1.0 zhanghc 2017年6月2日下午3:07:43
	 * @return List<Map<String,Object>>
	 *//*
	@RequestMapping("/paperCfgListQuestionTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> paperCfgListQuestionTypeTreeList() {
		try {
			return paperService.getQuestionTypeTreeList();
		} catch (Exception e) {
			log.error("获取配置试卷试题分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	*//**
	 * 配置试卷试题列表
	 * 
	 * v1.0 zhanghc 2017年5月27日下午2:39:34
	 * @param pageIn
	 * @return PageOut
	 *//*
	@RequestMapping("/paperCfgList")
	@ResponseBody
	public PageOut paperCfgList(PageIn pageIn) {
		try {
			return paperService.getPaperCfgListpage(pageIn);
		} catch (Exception e) {
			log.error("配置试卷试题列表错误：", e);
			return new PageOut();
		}
	}
	
	*//**
	 * 完成配置试卷添加试题
	 * 
	 * v1.0 zhanghc 2017年5月27日下午2:58:39
	 * @param paperQuestion
	 * @return PageResult
	 *//*
	@RequestMapping("/doPaperCfgListAdd")
	@ResponseBody
	public PageResult doPaperCfgListAdd(Integer paperId, Integer parentPaperQuestionId, Integer[] questionIds) {
		try {
			paperService.doPaperCfgListAdd(paperId, parentPaperQuestionId, questionIds, getCurrentUser());
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成配置试卷添加试题错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 完成配置试卷删除试题
	 * 
	 * v1.0 zhanghc 2017年5月27日下午5:47:55
	 * @param paperId
	 * @param parentPaperQuestionId
	 * @param questionIds
	 * @return
	 * PageResult
	 *//*
	@RequestMapping("/doPaperCfgDel")
	@ResponseBody
	public PageResult doPaperCfgDel(Integer paperQuestionId) {
		try {
			paperService.doPaperCfgDel(paperQuestionId);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成配置试卷删除试题错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达配置试卷试题排序页面
	 * 
	 * v1.0 zhanghc 2017年6月1日下午2:54:00
	 * @return PageOut
	 *//*
	@RequestMapping("/toPaperCfgSort")
	public String toPaperCfgSort(Model model, Integer paperQuestionId) {
		try {
			PaperQuestion paperQuestion = paperService.getPaperQuestion(paperQuestionId);
			List<PaperQuestion> paperQuestionList = paperService.getPaperQuestionList(paperQuestion.getParentId());
			model.addAttribute("maxNo", paperQuestionList.size());
			return "/WEB-INF/jsp/exam/paper/paperCfgSort.jsp";
		} catch (Exception e) {
			log.error("到达配置试卷试题排序页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperCfgSort.jsp";
		}
	}

	
	*//**
	 * 完成配置试卷试题排序
	 * 
	 * v1.0 zhanghc 2017年5月31日上午8:55:35
	 * @param sourceQuestionId
	 * @param targetQuestionId
	 * @return PageResult
	 *//*
	@RequestMapping("/doPaperCfgSort")
	@ResponseBody
	public PageResult doPaperCfgSort(Integer paperQuestionId, Integer no) {
		try {
			paperService.doPaperCfgSort(paperQuestionId, no);
			return new PageResult(true, "排序成功");
		} catch (Exception e) {
			log.error("完成配置试卷试题排序错误：", e);
			return new PageResult(false, "排序失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达配置试卷预览页面
	 * 
	 * v1.0 zhanghc 2017年6月4日下午9:07:02
	 * @return String
	 *//*
	@RequestMapping("/toPaperCfgPreview")
	public String toPaperCfgPreview(Model model, Integer id) {
		try {
			List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(id);
			model.addAttribute("id", id);
			model.addAttribute("paperQuestionExList", paperQuestionExList);
			model.addAttribute("questionOptions", DictCache.getIndexDictlistMap().get("QUESTION_OPTIONS"));
			return "/WEB-INF/jsp/exam/paper/paperCfgPreview.jsp";
		} catch (Exception e) {
			log.error("到达配置试卷预览页面错误：", e);
			return "/WEB-INF/jsp/exam/paper/paperCfgPreview.jsp";
		}
	}
	
	*//**
	 * 完成配置试卷设置分数
	 * 
	 * v1.0 zhanghc 2017年6月9日下午3:48:51
	 * @param paperQuestionId
	 * @param no
	 * @return PageResult
	 *//*
	@RequestMapping("/doPaperCfgScoreUpdate")
	@ResponseBody
	public PageResult doPaperCfgScoreUpdate(Integer[] paperQuestionIds, BigDecimal[] scores) {
		try {
			paperService.doPaperCfgScoreUpdate(paperQuestionIds, scores);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置分数错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}*/
}
