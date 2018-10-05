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
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.exam.service.QuestionService;
import com.wcpdoc.exam.sys.cache.DictCache;

/**
 * 试题控制层
 * 
 * v1.0 zhanghc 2018年6月3日上午10:34:21
 */
@Controller
@RequestMapping("/home/question")
public class HomeQuestionController extends BaseController {
	private static final Logger log = LoggerFactory
			.getLogger(HomeQuestionController.class);

	@Resource
	private QuestionService questionService;
	
	/**
	 * 到达试题列表页面 
	 * 
	 * v1.0 zhanghc 2017-05-07 14:56:29
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			model.addAttribute("QUESTION_TYPE", DictCache.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY", DictCache.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE", DictCache.getIndexDictlistMap().get("STATE"));
			model.addAttribute("QUESTION_OPTIONS", DictCache.getIndexDictlistMap().get("QUESTION_OPTIONS"));
			return "/WEB-INF/jsp/home/question/questionList.jsp";
		} catch (Exception e) {
			log.error("到达试题列表页面错误：", e);
			return "/WEB-INF/jsp/home/question/questionList.jsp";
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
	public List<Map<String, Object>> questionTypeTreeList() {
		try {
			return questionService.getQuestionTypeTreeList(getCurrentUser().getId());
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new ArrayList<Map<String,Object>>();
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
	public PageOut list(PageIn pageIn) {
		try {
			return questionService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("试题列表错误：", e);
			return new PageOut();
		}
	}
	
	/**
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
			return "/WEB-INF/jsp/home/question/questionEdit.jsp";
		} catch (Exception e) {
			log.error("到达添加试题页面错误：", e);
			return "/WEB-INF/jsp/home/question/questionEdit.jsp";
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
			question.setUpdateUserId(getCurrentUser().getId());
			questionService.save(question);
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
			
			return "/WEB-INF/jsp/home/question/questionEdit.jsp";
		} catch (Exception e) {
			log.error("到达修改试题页面错误：", e);
			return "/WEB-INF/jsp/home/question/questionEdit.jsp";
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
			entity.setUpdateUserId(getCurrentUser().getId());
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
			return "/WEB-INF/jsp/home/question/questionQuestionTypeUpdate.jsp";
		} catch (Exception e) {
			log.error("到达设置试题分类页面错误：", e);
			return "/WEB-INF/jsp/home/question/questionQuestionTypeUpdate.jsp";
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
	}

	

	*//**
	 * 获取试题分类数据
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @return List<Map<String,Object>>
	 *//*
	@RequestMapping("/questionTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> questionTypeTreeList() {
		try {
			LoginUser user = getCurrentUser();
			return questionService.getQuestionTypeTreeList(user.getId());
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	*//**
	 * 到达添加试题页面
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * 
	 * @param model
	 * @return String
	 *//*
	@RequestMapping("/toAdd")
	public String toAdd(Model model, Integer questionTypeId) {
		try {
			model.addAttribute("questionTypeId", questionTypeId);
			model.addAttribute("QUESTION_TYPE_DICT", DictCache
					.getIndexDictlistMap().get("QUESTION_TYPE"));
			model.addAttribute("QUESTION_DIFFICULTY_DICT", DictCache
					.getIndexDictlistMap().get("QUESTION_DIFFICULTY"));
			model.addAttribute("STATE_DICT", DictCache.getIndexDictlistMap()
					.get("STATE"));
			return "/WEB-INF/jsp/home/question/questionEdit.jsp";
		} catch (Exception e) {
			log.error("到达添加试题页面错误：", e);
			return "/WEB-INF/jsp/home/question/questionEdit.jsp";
		}
	}

	*//**
	 * 完成临时上传附件
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * @param files
	 * @return PageResult
	 *//*
	@RequestMapping("/doTempUpload")
	@ResponseBody
	public Map<String, Object> doTempUpload(@RequestParam("files") MultipartFile[] files) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String[] allowTypes = { "jpg", "gif", "png", "mp4", "JPG", "GIF", "PNG", "MP4"};
			String fileIds = questionService.doTempUpload(files, allowTypes, getCurrentUser(), request.getRemoteAddr());
			data.put("fileIds", fileIds);
			data.put("error", 0);
			data.put("url", "doDownload?fileId=" + fileIds);
			return data;
		} catch (Exception e) {
			log.error("完成临时上传附件失败：", e);
			data.put("error", 1);
			data.put("url", e.getMessage());
			return data;
		}
	}
	
	*//**
	 * 完成上传附件
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * @param fileIds
	 * @return PageResult
	 *//*
	@RequestMapping("/doUpload")
	@ResponseBody
	public PageResult doUpload(Integer[] fileIds) {
		StringBuilder message = new StringBuilder();
		try {
			for (Integer id : fileIds) {
				try {
					if (id == null) {
						continue;
					}
					questionService.doUpload(id, getCurrentUser(), request.getRemoteAddr());
				} catch (Exception e) {
					log.error("完成上传附件失败：", e);
					message.append(e.getMessage()).append("；");
				}
			}

			if (message.length() > 0) {
				throw new RuntimeException(message.toString());
			}
			
			return new PageResult(true, "完成上传附件成功");
		} catch (Exception e) {
			log.error("完成上传附件失败：", e);
			return new PageResult(false, "完成上传附件失败：" + message);
		}
	}
	
	*//**
	 * 完成下载附件
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * @param fileId
	 * void
	 *//*
	@RequestMapping(value = "/doDownload")
	public void doDownload(Integer fileId) {
		OutputStream output = null;
		try {
			FileEx fileEx = questionService.getFileEx(fileId);
			String fileName = new String((fileEx.getEntity().getName() + "." + fileEx.getEntity().getExtName()).getBytes("UTF-8"), "ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("application/force-download");

			output = response.getOutputStream();
			FileUtils.copyFile(fileEx.getFile(), output);
		} catch (Exception e) {
			log.error("完成下载附件失败：", e);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}*/
}
