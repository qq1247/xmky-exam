package com.wcpdoc.exam.home.controller;

import java.math.BigDecimal;
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
import com.wcpdoc.exam.exam.entity.Paper;
import com.wcpdoc.exam.exam.entity.PaperQuestion;
import com.wcpdoc.exam.exam.entity.PaperQuestionEx;
import com.wcpdoc.exam.exam.entity.PaperType;
import com.wcpdoc.exam.exam.service.PaperService;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 试卷控制层
 * 
 * v1.0 zhanghc 2018年10月13日上午10:49:20
 */
@Controller
@RequestMapping("/home/paper")
public class HomePaperController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(HomePaperController.class);
	
	@Resource
	private PaperService paperService;
	
	/**
	 * 到达试卷列表页面 
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			return "/WEB-INF/jsp/home/paper/paperList.jsp";
		} catch (Exception e) {
			log.error("到达试卷列表页面错误：", e);
			return "/WEB-INF/jsp/home/paper/paperList.jsp";
		}
	}
	
	/**
	 * 获取试卷分类树形列表
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/paperTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> getPaperTypeTreeList() {
		try {
			return paperService.getPaperTypeTreeList(getCurrentUser().getId());
		} catch (Exception e) {
			log.error("获取试卷分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 试卷列表 
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return pageOut
	 */
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
	
	/**
	 * 到达添加试卷页面 
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model, Integer paperTypeId) {
		try {
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			model.addAttribute("paperType", paperService.getPaperType2(paperTypeId));
			return "/WEB-INF/jsp/home/paper/paperEdit.jsp";
		} catch (Exception e) {
			log.error("到达添加试卷页面错误：", e);
			return "/WEB-INF/jsp/home/paper/paperEdit.jsp";
		}
	}
	
	/**
	 * 完成添加试卷
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return pageOut
	 */
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
	
	/**
	 * 到达修改试卷页面 
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			model.addAttribute("paper", paper);
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			
			PaperType paperType = paperService.getPaperType(id);
			model.addAttribute("paperType", paperType);
			return "/WEB-INF/jsp/home/paper/paperEdit.jsp";
		} catch (Exception e) {
			log.error("到达修改试卷页面错误：", e);
			return "/WEB-INF/jsp/home/paper/paperEdit.jsp";
		}
	}
	
	/**
	 * 完成修改试卷
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return pageOut
	 */
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
	
	/**
	 * 完成删除试卷
	 * 
	 * v1.0 zhanghc 2018年10月13日上午10:49:20
	 * @return pageOut
	 */
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
	
	/**
	 * 到达配置试卷页面
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:06
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toCfg")
	public String toCfg(Model model, Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			model.addAttribute("paper", paper);
			List<PaperQuestionEx> paperQuestionExList = paperService.getPaperList(id);
			model.addAttribute("paperQuestionExList", paperQuestionExList);
			return "/WEB-INF/jsp/home/paper/paperCfg.jsp";
		} catch (Exception e) {
			log.error("到达配置试卷页面错误：", e);
			return "/WEB-INF/jsp/home/paper/paperCfg.jsp";
		}
	}
	
	/**
	 * 到达添加章节页面
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:13
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toChapterAdd")
	public String toChapterAdd(Model model, Integer id) {
		try {
			PaperQuestion chapter = new PaperQuestion();
			chapter.setPaperId(id);
			model.addAttribute("chapter", chapter);
			return "/WEB-INF/jsp/home/paper/chapterEdit.jsp";
		} catch (Exception e) {
			log.error("到达添加章节页面错误：", e);
			return "/WEB-INF/jsp/home/paper/chapterEdit.jsp";
		}
	}
	
	/**
	 * 完成添加章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:22
	 * @param chapter
	 * @return PageResult
	 */
	@RequestMapping("/doChapterAdd")
	@ResponseBody
	public PageResult doChapterAdd(PaperQuestion chapter) {
		try {
			paperService.doChapterAdd(chapter, getCurrentUser());
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加章节错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达修改章节页面
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:29
	 * @param model
	 * @param chapterId
	 * @return String
	 */
	@RequestMapping("/toChapterEdit")
	public String toChapterEdit(Model model, Integer chapterId) {
		try {
			PaperQuestion chapter = paperService.getPaperQuestion(chapterId);
			model.addAttribute("chapter", chapter);
			return "/WEB-INF/jsp/home/paper/chapterEdit.jsp";
		} catch (Exception e) {
			log.error("到达修改章节页面错误：", e);
			return "/WEB-INF/jsp/home/paper/chapterEdit.jsp";
		}
	}
	
	/**
	 * 完成修改章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:35
	 * @param chapter
	 * @return PageResult
	 */
	@RequestMapping("/doChapterEdit")
	@ResponseBody
	public PageResult doChapterEdit(PaperQuestion chapter) {
		try {
			paperService.doChapterEdit(chapter, getCurrentUser());
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改章节错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成删除章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:46
	 * @param model
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/doChapterDel")
	@ResponseBody
	public PageResult doChapterDel(Model model, Integer chapterId) {
		try {
			paperService.doChapterDel(chapterId);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除章节错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达添加试题页面
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:53
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toQuestionAdd")
	public String toQuestionAdd(Model model, Integer id) {
		try {
			model.addAttribute("id", id);
			model.addAttribute("QUESTION_TYPE_DICT", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY_DICT", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap().get("STATE"));
			return "/WEB-INF/jsp/home/paper/questionAdd.jsp";
		} catch (Exception e) {
			log.error("到达添加试题页面错误：", e);
			return "/WEB-INF/jsp/home/paper/questionAdd.jsp";
		}
	}
	
	/**
	 * 获取试题分类数据
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:00
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/questionTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> questionTypeTreeList() {
		try {
			return paperService.getQuestionTypeTreeList(getCurrentUser().getId());
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	/**
	 * 试题列表
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:09
	 * @param pageIn
	 * @return PageOut
	 */
	@RequestMapping("/questionList")
	@ResponseBody
	public PageOut questionList(PageIn pageIn) {
		try {
			return paperService.getQuestionListpage(pageIn);
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
	 * 完成添加试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:26
	 * @param chapterId
	 * @param questionIds
	 * @return PageResult
	 */
	@RequestMapping("/doQuestionAdd")
	@ResponseBody
	public PageResult doQuestionAdd(Integer chapterId, Integer[] questionIds) {
		try {
			paperService.doQuestionAdd(chapterId, questionIds, getCurrentUser());
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加试题错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成清空试题
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:17:32
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/doQuestionClear")
	@ResponseBody
	public PageResult doQuestionClear(Integer chapterId) {
		try {
			paperService.doQuestionClear(chapterId, getCurrentUser());
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加试题错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	/**
	 * 到达设置分数页面
	 * 
	 * v1.0 zhanghc 2018年10月21日上午9:00:27
	 * @param model
	 * @param id
	 * @return String
	 */
	@RequestMapping("/toScoresUpdate")
	public String toScoresUpdate(Model model, Integer chapterId) {
		try {
			model.addAttribute("chapterId", chapterId);
			return "/WEB-INF/jsp/home/paper/scoresUpdate.jsp";
		} catch (Exception e) {
			log.error("到达设置分数页面错误：", e);
			return "/WEB-INF/jsp/home/paper/scoresUpdate.jsp";
		}
	}
	
	/**
	 * 完成设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:44:27
	 * @param chapterId
	 * @param scores
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/doScoresUpdate")
	@ResponseBody
	public PageResult doScoresUpdate(Integer chapterId, BigDecimal score, String options) {
		try {
			paperService.doScoresUpdate(chapterId, score, options);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置分数错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成章节上移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/doChapterUp")
	@ResponseBody
	public PageResult doChapterUp(Integer chapterId) {
		try {
			paperService.doChapterUp(chapterId);
			return new PageResult(true, "移动成功");
		} catch (Exception e) {
			log.error("完成章节上移错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成章节下移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/doChapterDown")
	@ResponseBody
	public PageResult doChapterDown(Integer chapterId) {
		try {
			paperService.doChapterDown(chapterId);
			return new PageResult(true, "移动成功");
		} catch (Exception e) {
			log.error("完成章节下移错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @param score
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/doScoreUpdate")
	@ResponseBody
	public PageResult doScoreUpdate(Integer paperQuestionId, BigDecimal score, String options) {
		try {
			paperService.doScoreUpdate(paperQuestionId, score, options);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置分数错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成试题上移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @return PageResult
	 */
	@RequestMapping("/doQuestionUp")
	@ResponseBody
	public PageResult doQuestionUp(Integer paperQuestionId) {
		try {
			paperService.doQuestionUp(paperQuestionId);
			return new PageResult(true, "移动成功");
		} catch (Exception e) {
			log.error("完成试题上移错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成试题下移
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @return PageResult
	 */
	@RequestMapping("/doQuestionDown")
	@ResponseBody
	public PageResult doQuestionDown(Integer paperQuestionId) {
		try {
			paperService.doQuestionDown(paperQuestionId);
			return new PageResult(true, "移动成功");
		} catch (Exception e) {
			log.error("完成试题下移错误：", e);
			return new PageResult(false, "移动失败：" + e.getMessage());
		}
	}
	
	/**
	 * 完成试题删除
	 * 
	 * v1.0 zhanghc 2018年10月21日下午10:41:34
	 * @param paperQuestionId
	 * @return PageResult
	 */
	@RequestMapping("/doQuestionDel")
	@ResponseBody
	public PageResult doQuestionDel(Integer paperQuestionId) {
		try {
			paperService.doQuestionDel(paperQuestionId);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成试题删除错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
}
