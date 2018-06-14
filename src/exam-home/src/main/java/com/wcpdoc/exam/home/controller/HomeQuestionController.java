package com.wcpdoc.exam.home.controller;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.LoginUser;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.file.entity.FileEx;
import com.wcpdoc.exam.home.service.HomeQuestionService;
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
	private HomeQuestionService homeQuestionService;

	/**
	 * 到达试题列表页面
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			return "/WEB-INF/jsp/home/question/questionList.jsp";
		} catch (Exception e) {
			log.error("到达试题列表页面错误：", e);
			return "/WEB-INF/jsp/home/question/questionList.jsp";
		}
	}

	/**
	 * 获取试题分类数据
	 * 
	 * v1.0 zhanghc 2017-05-25 16:34:59
	 * 
	 * @return List<Map<String,Object>>
	 */
	@RequestMapping("/questionTypeTreeList")
	@ResponseBody
	public List<Map<String, Object>> questionTypeTreeList() {
		try {
			LoginUser user = getCurrentUser();
			return homeQuestionService.getQuestionTypeTreeList(user.getId());
		} catch (Exception e) {
			log.error("获取试题分类树错误：", e);
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 到达添加试题页面
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * 
	 * @param model
	 * @return String
	 */
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

	/**
	 * 完成临时上传附件
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * @param files
	 * @return PageResult
	 */
	@RequestMapping("/doTempUpload")
	@ResponseBody
	public Map<String, Object> doTempUpload(@RequestParam("files") MultipartFile[] files) {
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String[] allowTypes = { "jpg", "gif", "png", "mp4", "JPG", "GIF", "PNG", "MP4"};
			String fileIds = homeQuestionService.doTempUpload(files, allowTypes, getCurrentUser(), request.getRemoteAddr());
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
	
	/**
	 * 完成上传附件
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * @param fileIds
	 * @return PageResult
	 */
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
					homeQuestionService.doUpload(id, getCurrentUser(), request.getRemoteAddr());
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
	
	/**
	 * 完成下载附件
	 * 
	 * v1.0 zhanghc 2018年6月7日下午9:30:03
	 * @param fileId
	 * void
	 */
	@RequestMapping(value = "/doDownload")
	public void doDownload(Integer fileId) {
		OutputStream output = null;
		try {
			FileEx fileEx = homeQuestionService.getFileEx(fileId);
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
	}
}
