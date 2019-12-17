package com.wcpdoc.exam.log.controller;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageOut;
import com.wcpdoc.exam.log.service.LogService;

/**
 * 日志控制层
 * 
 * v1.0 zhanghc 2017年4月20日下午11:53:05
 */
@Controller
@RequestMapping("/log")
public class LogController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(LogController.class);

	@Resource
	private LogService logService;
	@Value("${file.log.dir}")
	private String logDir;

	/**
	 * 到达日志列表页面
	 * 
	 * v1.0 zhanghc 2017年4月20日下午11:53:05
	 * 
	 * @return String
	 */
	@RequestMapping("/toList")
	public String toList(Model model) {
		try {
			List<File> fileList = (List<File>) FileUtils.listFiles(new File(logDir), null, false);
			Collections.sort(fileList, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					long l = o2.lastModified() - o1.lastModified();
					return l >= 0 ? 1 : -1;
				}
			});

			model.addAttribute("fileList", fileList);
			return "log/log/logList";
		} catch (Exception e) {
			log.error("到达日志列表页面错误：", e);
			return "log/log/logList";
		}
	}

	/**
	 * 日志列表
	 * 
	 * v1.0 zhanghc 2017年4月20日下午11:53:05
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageOut list(PageIn pageIn) {
		try {
			return logService.getListpage(pageIn);
		} catch (Exception e) {
			log.error("日志列表错误：", e);
			return new PageOut();
		}
	}
}
