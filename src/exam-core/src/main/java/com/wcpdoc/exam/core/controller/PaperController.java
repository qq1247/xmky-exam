package com.wcpdoc.exam.core.controller;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Paper;
import com.wcpdoc.exam.core.entity.PaperQuestion;
import com.wcpdoc.exam.core.entity.PaperQuestionEx;
import com.wcpdoc.exam.core.entity.PaperType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.PaperQuestionService;
import com.wcpdoc.exam.core.service.PaperService;
import com.wcpdoc.exam.core.service.PaperTypeService;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;
/**
 * 试卷控制层
 * 
 * zhanghc 2018年10月21日上午8:16:06
 */
@Controller
@RequestMapping("/paper")
public class PaperController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(PaperController.class);
	
	@Resource
	private PaperService paperService;
	@Resource
	private PaperTypeService paperTypeService;
	@Resource
	private PaperQuestionService paperQuestionService;
	@Resource
	private QuestionTypeService questionTypeService;
	@Resource
	private QuestionService questionService;
	
	/**
	 * 到达试卷列表页面
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList() {
		try {
			return "exam/paper/paperList";
		} catch (Exception e) {
			log.error("到达试卷列表页面错误：", e);
			return "exam/paper/paperList";
		}
	}
	
	/**
	 * 试卷分类树
	 * v1.0 zhanghc 2016-5-24下午14:54:09
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/paperTypeTreeList")
	@ResponseBody
	public PageResult paperTypeTreeList() {
		try {
			return new PageResultEx(true, "查询成功", paperTypeService.getAuthTreeList());
		} catch (Exception e) {
			log.error("试卷分类树错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 试卷列表
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.setTen(getCurUser().getId().toString());
			}
			return new PageResultEx(true, "查询成功", paperService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试卷列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 到达添加试卷页面
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			Paper paper = new Paper();
			model.addAttribute("paper", paper);
			model.addAttribute("QUESTION_TYPE_DICT_LIST", DictCache.getIndexDictlistMap().get("PAPER_PREVIEW_TYPE"));
			return "exam/paper/paperEdit";
		} catch (Exception e) {
			log.error("到达添加试卷页面错误：", e);
			return "exam/paper/paperEdit";
		}
	}
	
	/**
	 * 完成添加试卷
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Paper paper) {
		try {
			paper.setUpdateUserId(getCurUser().getId());
			paper.setUpdateTime(new Date());
			paper.setTotalScore(BigDecimal.ZERO);
			paper.setState(2);
			paperService.add(paper);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加试卷错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试卷错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 到达修改试卷页面
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			model.addAttribute("paper", paper);
			
			PaperType paperType = paperTypeService.getEntity(paper.getPaperTypeId());
			model.addAttribute("paperType", paperType);
			return "exam/paper/paperEdit";
		} catch (Exception e) {
			log.error("到达修改试卷页面错误：", e);
			return "exam/paper/paperEdit";
		}
	}
	
	/**
	 * 完成修改试卷
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Paper paper) {
		try {
			Paper entity = paperService.getEntity(paper.getId());
//			entity.setPaperTypeId(paper.getPaperTypeId());//不需要修改
			entity.setName(paper.getName());
			entity.setPreviewType(paper.getPreviewType());
			entity.setPassScore(paper.getPassScore());
			entity.setScoreA(paper.getScoreA());
			entity.setScoreARemark(paper.getScoreARemark());
			entity.setScoreB(paper.getScoreB());
			entity.setScoreBRemark(paper.getScoreBRemark());
			entity.setScoreC(paper.getScoreC());
			entity.setScoreCRemark(paper.getScoreCRemark());
			entity.setScoreD(paper.getScoreD());
			entity.setScoreDRemark(paper.getScoreDRemark());
			entity.setScoreE(paper.getScoreE());
			entity.setScoreERemark(paper.getScoreERemark());
			entity.setDescription(paper.getDescription());
			//entity.setState(paper.getState());//单独控制
			entity.setUpdateUserId(getCurUser().getId());
			entity.setUpdateTime(new Date());
			paperService.update(entity);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("完成修改试卷错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成修改试卷错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 完成删除试卷
	 * 
	 * zhanghc 2018年10月21日上午8:16:06
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			Paper paper = paperService.getEntity(id);
			paper.setState(0);
			paper.setUpdateTime(new Date());
			paper.setUpdateUserId(getCurUser().getId());
			paperService.update(paper);
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除试卷错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除试卷错误：", e);
			return new PageResult(false, "未知异常");
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
			model.addAttribute("id", id);
			model.addAttribute("design", true);// 控制页面展示那部分
			model.addAttribute("answer", false);// 控制页面展示那部分
			return "exam/paper/paperCfg";
		} catch (Exception e) {
			log.error("到达配置试卷页面错误：", e);
			return "exam/paper/paperCfg";
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
			return "exam/paper/chapterEdit";
		} catch (Exception e) {
			log.error("到达添加章节页面错误：", e);
			return "exam/paper/chapterEdit";
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
			paperService.doChapterAdd(chapter);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加章节错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加章节错误：", e);
			return new PageResult(false, "未知异常！");
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
			PaperQuestion chapter = paperQuestionService.getEntity(chapterId);
			model.addAttribute("chapter", chapter);
			return "exam/paper/chapterEdit";
		} catch (Exception e) {
			log.error("到达修改章节页面错误：", e);
			return "exam/paper/chapterEdit";
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
			paperService.doChapterEdit(chapter);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改章节错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
	
	/**
	 * 完成删除章节
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:46
	 * @param chapterId
	 * @return PageResult
	 */
	@RequestMapping("/doChapterDel")
	@ResponseBody
	public PageResult doChapterDel(Integer chapterId) {
		try {
			paperService.doChapterDel(chapterId);
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除章节错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成删除章节错误：", e);
			return new PageResult(false, "未知异常！");
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
		} catch (MyException e) {
			log.error("完成章节上移错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成章节上移错误：", e);
			return new PageResult(false, "未知异常！");
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
		} catch (MyException e) {
			log.error("完成章节下移错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成章节下移错误：", e);
			return new PageResult(false, "未知异常！");
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
			model.addAttribute("QUESTION_TYPE_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE_DICT_LIST", DictCache.getIndexDictlistMap().get("STATE"));
			return "exam/paper/questionAdd";
		} catch (Exception e) {
			log.error("到达添加试题页面错误：", e);
			return "exam/paper/questionAdd";
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
	public PageResult questionTypeTreeList() {
		try {
			return new PageResultEx(true, "查询成功", questionTypeService.getAuthTreeList());
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new PageResult(false, "查询失败");
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
	public PageResult questionList(PageIn pageIn) {
		try {
			if (getCurUser().getId() != 1) {
				pageIn.setFour("1");
				pageIn.setTen(getCurUser().getId().toString());
			}

			return new PageResultEx(true, "查询成功", questionService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return new PageResult(false, "查询失败");
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
			paperService.doQuestionAdd(chapterId, questionIds);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加试题错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试题错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
	
	/**
	 * 设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @param score
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/doScoreUpdate")
	@ResponseBody
	public PageResult doScoreUpdate(Integer paperQuestionId, BigDecimal score) {
		try {
			paperService.doScoreUpdate(paperQuestionId, score);
			return new PageResult(true, "设置成功");
		} catch (MyException e) {
			log.error("完成设置分数错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成设置分数错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
	
	/**
	 * 完成设置选项
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param paperQuestionId
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/doOptionsUpdate")
	@ResponseBody
	public PageResult doOptionsUpdate(Integer paperQuestionId, Integer[] options) {
		try {
			paperService.doOptionsUpdate(paperQuestionId, options);
			return new PageResult(true, "设置成功");
		} catch (MyException e) {
			log.error("完成设置分数错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成设置分数错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
	
	/**
	 * 到达批量设置分数页面
	 * 
	 * v1.0 zhanghc 2018年10月21日上午8:16:53
	 * @param model
	 * @param chapterId
	 * @return String
	 */
	@RequestMapping("/toBatchScoreUpdate")
	public String toBatchScoreUpdate(Model model, Integer chapterId) {
		try {
			model.addAttribute("chapterId", chapterId);
			return "exam/paper/batchScoreUpdate";
		} catch (Exception e) {
			log.error("到达批量设置分数页面错误：", e);
			return "exam/paper/batchScoreUpdate";
		}
	}
	
	/**
	 * 完成批量设置分数
	 * 
	 * v1.0 zhanghc 2018年10月21日上午10:46:54
	 * @param chapterId
	 * @param score
	 * @param options
	 * @return PageResult
	 */
	@RequestMapping("/doBatchScoreUpdate")
	@ResponseBody
	public PageResult doBatchScoreUpdate(Integer chapterId, BigDecimal score, String options) {
		try {
			paperService.doBatchScoreUpdate(chapterId, score, options);
			return new PageResult(true, "设置成功");
		} catch (MyException e) {
			log.error("完成设置分数错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成设置分数错误：", e);
			return new PageResult(false, "未知异常！");
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
		} catch (MyException e) {
			log.error("完成试题上移错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成试题上移错误：", e);
			return new PageResult(false, "未知异常！");
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
		} catch (MyException e) {
			log.error("完成试题下移错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成试题下移错误：", e);
			return new PageResult(false, "未知异常！");
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
		} catch (MyException e) {
			log.error("完成试题删除错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成试题删除错误：", e);
			return new PageResult(false, "未知异常！");
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
			paperService.doQuestionClear(chapterId);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加试题错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成添加试题错误：", e);
			return new PageResult(false, "未知异常！");
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
			Paper paper = paperService.getEntity(id);
			if(paper.getState() == 0) {
				throw new MyException("试卷【"+paper.getName()+"】已删除！");
			}
			if(paper.getState() == 1) {
				throw new MyException("试卷【"+paper.getName()+"】已发布！");
			}
			
			paper.setState(1);
			paperService.update(paper);
			return new PageResult(true, "发布成功");
		} catch (MyException e) {
			log.error("完成发布错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("完成发布错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
}
