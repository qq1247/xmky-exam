package com.wcpdoc.exam.log.controller;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
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
			// 从log4j.xml解析日志文件目录
			File log4j2File = new File(ResourceUtils.getURL("classpath:log4j2.xml").getFile());
			Document parse = Jsoup.parse(log4j2File, "UTF-8");
			Elements elementsByTag = parse.getElementsByTag("RollingFile");
			File logDir = new File(elementsByTag.attr("filepattern")).getParentFile();
			logService.setLogDir(logDir);
			
			// 获取所有日志文件
			List<File> logFileList = (List<File>) FileUtils.listFiles(logDir, null, false);
			Collections.sort(logFileList, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					long l = o2.lastModified() - o1.lastModified();
					return l >= 0 ? 1 : -1;
				}
			});

			model.addAttribute("logFileList", logFileList);
			return "/log/log/logList";
		} catch (Exception e) {
			log.error("到达日志列表页面错误：", e);
			return "/log/log/logList";
		}
	}

	/**
	 * 日志列表
	 * 
	 * v1.0 zhanghc 2017年4月20日下午11:53:05
	 * 
	 * @param logName 日志文件名称
	 * @param curReadLen 当前读取日志文件字节数
	 * @return PageResult
	 */
	@RequestMapping("/list")
	@ResponseBody
	public PageResult list(String logName, Long curReadLen) {
		try {
			return new PageResultEx(true, "查询成功", logService.getList(logName, curReadLen));
		} catch (Exception e) {
			log.error("日志列表错误：", e);
			return new PageResult(false, "查询失败");
		}
	}
}
