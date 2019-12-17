package com.wcpdoc.exam.core.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.service.QuestionService;

/**
 * 试题控制层
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Controller
@RequestMapping("/question")
@Deprecated
public class QuestionController extends BaseController{
//	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);
	
	@Resource
	private QuestionService questionService;
	
	/**
	 * 到达试题列表页面 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 *//*
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			model.addAttribute("QUESTION_TYPE", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
			model.addAttribute("QUESTION_OPTIONS", DictCache.getIndexDictlistMap().get("QUESTION_OPTIONS"));
			return "exam/question/questionList";
		} catch (Exception e) {
			log.error("到达试题列表页面错误：", e);
			return "exam/question/questionList";
		}
	}
	
	*//**
	 * 获取试题分类数据
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return List<Map<String,Object>>
	 *//*
	@RequestMapping("/questionTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> questionTypeTreeList() {
		try {
			return questionService.getQuestionTypeTreeList();
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	*//**
	 * 试题列表 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 *//*
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return questionService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return new PageOut();
		}
	}
	
	*//**
	 * 到达添加试题页面 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 *//*
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			model.addAttribute("QUESTION_TYPE", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
			return "exam/question/questionEdit";
		} catch (Exception e) {
			log.error("到达添加试题页面错误：", e);
			return "exam/question/questionEdit";
		}
	}
	
	*//**
	 * 完成添加试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 *//*
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Question question) {
		try {
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			questionService.add(question);
			return new PageResult(true, "添加成功");
		} catch (Exception e) {
			log.error("完成添加试题错误：", e);
			return new PageResult(false, "添加失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达修改试题页面 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 *//*
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Question question = questionService.getEntity(id);
			model.addAttribute("question", question);
			QuestionType questionType = questionService.getQuestionType(id);
			model.addAttribute("questionType", questionType);
			model.addAttribute("QUESTION_TYPE", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
			
			return "exam/question/questionEdit";
		} catch (Exception e) {
			log.error("到达修改试题页面错误：", e);
			return "exam/question/questionEdit";
		}
	}
	
	*//**
	 * 完成修改试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 *//*
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Question question) {
		try {
			Question entity = questionService.getEntity(question.getId());
			entity.setUpdateTime(new Date());
			entity.setUpdateUserId(getCurUser().getId());
//			entity.setType(question.getType());//不允许修改类型
			entity.setState(question.getState());
			entity.setDifficulty(question.getDifficulty());
			entity.setTitle(question.getTitle());
			entity.setOptionA(question.getOptionA());
			entity.setOptionB(question.getOptionB());
			entity.setOptionC(question.getOptionC());
			entity.setOptionD(question.getOptionD());
			entity.setOptionE(question.getOptionE());
			entity.setOptionF(question.getOptionF());
			entity.setOptionG(question.getOptionG());
			entity.setAnswer(question.getAnswer());
			entity.setAnalysis(question.getAnalysis());
			
			questionService.update(entity);
			return new PageResult(true, "修改成功");
		} catch (Exception e) {
			log.error("完成修改试题错误：", e);
			return new PageResult(false, "修改失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 完成删除试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 *//*
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer[] ids) {
		try {
			questionService.delAndUpdate(ids);
			return new PageResult(true, "删除成功");
		} catch (Exception e) {
			log.error("完成删除试题错误：", e);
			return new PageResult(false, "删除失败：" + e.getMessage());
		}
	}
	
	*//**
	 * 到达设置试题分类页面
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 *//*
	@RequestMapping("/toQuestionTypeUpdate")
	public String toQuestionTypeUpdate() {
		try {
			return "exam/question/questionQuestionTypeUpdate";
		} catch (Exception e) {
			log.error("到达设置试题分类页面错误：", e);
			return "exam/question/questionQuestionTypeUpdate";
		}
	}
	
	*//**
	 * 获取试题分类树
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return List<Map<String,Object>>
	 *//*
	@RequestMapping("/questionTypeUpdateQuestionTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> questionTypeUpdateTreeList() {
		try {
			return questionService.getQuestionTypeTreeList();
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
		}
	}
	
	*//**
	 * 完成设置试题分类
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param ids 用户ID
	 * @param questionTypeId 试题分类ID
	 * @return PageResult
	 *//*
	@RequestMapping("/doQuestionTypeUpdate")
	@ResponseBody
	public PageResult doQuestionTypeUpdate(Integer[] ids, Integer questionTypeId) {
		try {
			questionService.doQuestionTypeUpdate(ids, questionTypeId);
			return new PageResult(true, "设置成功");
		} catch (Exception e) {
			log.error("完成设置试题分类错误：", e);
			return new PageResult(false, "设置失败：" + e.getMessage());
		}
	}*/
}
