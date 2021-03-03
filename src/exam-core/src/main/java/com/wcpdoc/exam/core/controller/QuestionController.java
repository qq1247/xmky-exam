package com.wcpdoc.exam.core.controller;

import java.io.OutputStream;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.base.cache.DictCache;
import com.wcpdoc.exam.core.constant.ConstantManager;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.entity.Question;
import com.wcpdoc.exam.core.entity.QuestionType;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.core.service.QuestionService;
import com.wcpdoc.exam.core.service.QuestionTypeService;

/**
 * 试题控制层
 * 
 * v1.0 zhanghc 2017-05-07 14:56:29
 */
@Controller
@RequestMapping("/question")
public class QuestionController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

	@Resource
	private QuestionService questionService;
	@Resource
	private QuestionTypeService questionTypeService;
	
	/**
	 * 到达试题列表页面 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param model
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			model.addAttribute("QUESTION_TYPE_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE_DICT_LIST", DictCache.getIndexDictlistMap().get("STATE"));
			return "exam/question/questionList";
		} catch (Exception e) {
			log.error("到达试题列表页面错误：", e);
			return "exam/question/questionList";
		}
	}
	
	/**
	 * 获取试题分类数据
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/questionTypeTreeList")
	@ResponseBody
	public PageResult questionTypeTreeList() {
		try {
			return null; // new PageResultEx(true, "查询成功", questionTypeService.getAuthTreeList())
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 试题列表 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(PageIn pageIn) {
		try {
			if(!ConstantManager.ADMIN_LOGIN_NAME.equals(getCurUser().getLoginName())) {
				pageIn.setTen(getCurUser().getId().toString());
			}
			return new PageResultEx(true, "查询成功",  questionService.getListpage(pageIn));
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
	
	/**
	 * 到达添加试题页面 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 */
	@RequestMapping("/toAdd")
	public String toAdd(Model model) {
		try {
			model.addAttribute("QUESTION_TYPE_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE_DICT_LIST", DictCache.getIndexDictlistMap().get("STATE"));
			
			Question question = new Question();
			question.setType(1);
			question.setDifficulty(3);
			question.setState(1);
			model.addAttribute("question", question);
			return "exam/question/questionEdit";
		} catch (Exception e) {
			log.error("到达添加试题页面错误：", e);
			return "exam/question/questionEdit";
		}
	}
	
	/**
	 * 完成添加试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return pageOut
	 */
	@RequestMapping("/doAdd")
	@ResponseBody
	public PageResult doAdd(Question question) {
		try {
			questionService.addAndUpdate(question);
			return new PageResult(true, "添加成功");
		} catch (MyException e) {
			log.error("完成添加试题错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		}  catch (Exception e) {
			log.error("完成添加试题错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 到达修改试题页面 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 */
	@RequestMapping("/toEdit")
	public String toEdit(Model model, Integer id) {
		try {
			Question question = questionService.getEntity(id);
			model.addAttribute("question", question);
			
			QuestionType questionType = questionTypeService.getEntity(question.getQuestionTypeId());
			model.addAttribute("questionType", questionType);
			
			model.addAttribute("QUESTION_TYPE_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY_DICT_LIST", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE_DICT_LIST", DictCache.getIndexDictlistMap().get("STATE"));
			
			return "exam/question/questionEdit";
		} catch (Exception e) {
			log.error("到达修改试题页面错误：", e);
			return "exam/question/questionEdit";
		}
	}
	
	/**
	 * 完成修改试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param question 试题
	 * @param newVer 新版本
	 * @return PageResult
	 */
	@RequestMapping("/doEdit")
	@ResponseBody
	public PageResult doEdit(Question question, boolean newVer) {
		try {
			questionService.updateAndUpdate(question, newVer);
			return new PageResult(true, "修改成功");
		} catch (MyException e) {
			log.error("完成修改试题错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		}  catch (Exception e) {
			log.error("完成修改试题错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * 完成删除试题
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @param id
	 * @return pageOut
	 */
	@RequestMapping("/doDel")
	@ResponseBody
	public PageResult doDel(Integer id) {
		try {
			Question question = questionService.getEntity(id);
			question.setState(0);
			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			questionService.update(question);
			return new PageResult(true, "删除成功");
		} catch (MyException e) {
			log.error("完成删除试题错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		}  catch (Exception e) {
			log.error("完成删除试题错误：", e);
			return new PageResult(false, "未知异常");
		}
	}
	
	/**
	 * word试题导入
	 * 
	 * v1.0 zhanghc 2019年8月10日下午5:12:53
	 * @param file
	 * @param questionTypeId
	 * @return PageResult
	 */
	@RequestMapping("/wordImp")
	@ResponseBody
	public PageResult wordImp(@RequestParam("file") MultipartFile file, Integer questionTypeId) {
		try {
			questionService.wordImp(file, questionTypeId);
			return new PageResult(true, "导入成功");
		} catch (MyException e) {
			log.error("导入试题错误：{}", e.getMessage());
			return new PageResult(false, e.getMessage());
		} catch (Exception e) {
			log.error("导入试题错误：", e);
			return new PageResult(false, "未知异常！");
		}
	}
	
	/**
	 * word模板导出
	 * 
	 * v1.0 zhanghc 2019年8月14日下午5:33:20 
	 * void
	 */
	@RequestMapping(value = "/wordTemplateExport")
	public void wordTemplateExport() {
		OutputStream output = null;
		try {
			java.io.File file = new java.io.File(this.getClass().getResource("/").getPath() + "res/试题模板.doc");
			
			String fileName = new String((file.getName()).getBytes("UTF-8"), "ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("application/force-download");

			output = response.getOutputStream();
			FileUtils.copyFile(file, output);
		} catch (MyException e) {
			log.error("完成下载模板失败：{}", e.getMessage());
		} catch (Exception e) {
			log.error("完成下载模板失败：", e);
		} finally {
			IOUtils.closeQuietly(output);
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
			Question question = questionService.getEntity(id);
			if (question.getState() == 0) {
				throw new MyException("试题已删除！");
			}
			if (question.getState() == 1) {
				question.setState(2);
			} else if (question.getState() == 2) {
				question.setState(1);
			}

			question.setUpdateTime(new Date());
			question.setUpdateUserId(getCurUser().getId());
			questionService.update(question);
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
