package com.wcpdoc.exam.api.controller;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.exam.core.controller.BaseController;
import com.wcpdoc.exam.core.entity.PageIn;
import com.wcpdoc.exam.core.entity.PageResult;
import com.wcpdoc.exam.core.entity.PageResultEx;
import com.wcpdoc.exam.core.exception.MyException;
import com.wcpdoc.exam.file.entity.File;
import com.wcpdoc.exam.file.entity.FileEx;
import com.wcpdoc.exam.file.service.FileService;

/**
 * 附件控制层
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
@Controller
@RequestMapping("/api/file")
public class ApiFileController extends BaseController {
	private static final Logger log = LoggerFactory.getLogger(ApiFileController.class);

	@Resource
	private FileService fileService;

	/**
	 * 附件列表
	 * 
	 * v1.0 zhanghc 2016-11-16下午10:13:48
	 * 
	 * @return pageOut
	 */
	@RequestMapping("/listpage")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult listpage() {
		try {
			return PageResultEx.ok().data(fileService.getListpage(new PageIn(request)));
		} catch (Exception e) {
			log.error("附件列表错误：", e);
			return PageResult.err();
		}
	}

	/**
	 * 完成临时上传附件
	 * 
	 * v1.0 zhanghc 2017年3月6日下午11:51:18
	 * 
	 * @param files
	 * @return PageResult
	 */
	@RequestMapping("/upload")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult upload(@RequestParam("files") MultipartFile[] files) {
		try {
			String[] allowTypes = { "jpg", "gif", "png", "zip", "rar", "doc", "xls", "docx", "xlsx", "mp4" };
			String fileIds = fileService.doTempUpload(files, allowTypes);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("fileIds", fileIds);
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("完成临时上传附件失败：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成临时上传附件失败：", e);
			return PageResult.err();
		}
	}

	/**
	 * 完成上传附件
	 * 
	 * v1.0 zhanghc 2017年3月6日下午11:51:02
	 * 
	 * @param fileIds
	 * @return PageResult
	 */
	@RequestMapping("/uploadAdd")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult uploadAdd(Integer[] ids) {
		StringBuilder message = new StringBuilder();
		try {
			for (Integer id : ids) {
				try {
					if (id == null) {
						continue;
					}
					fileService.doUpload(id);
				} catch (Exception e) {
					log.error("完成上传附件失败：", e);
					message.append(e.getMessage()).append("；");
				}
			}

			if (message.length() > 0) {
				throw new MyException(message.toString());
			}

			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成上传附件失败：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成上传附件失败：", e);
			return PageResult.err();
		}
	}

	/**
	 * 完成下载附件
	 * 
	 * v1.0 zhanghc 2017年3月29日下午10:18:28 
	 * 使用spring ResponseEntity <byte[]>方式，附件大会造成内存溢出。
	 * 
	 * @param id
	 * void
	 */
	@RequestMapping(value = "/download")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public void download(Integer id) {
		OutputStream output = null;
		try {
			FileEx fileEx = fileService.getFileEx(id);
			String fileName = new String((fileEx.getEntity().getName() + "." 
					+ fileEx.getEntity().getExtName()).getBytes("UTF-8"), "ISO-8859-1");
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

	/**
	 * 完成删除附件<br/>
	 * 只负责逻辑删除，具体的由定时任务处理。<br/>
	 * 
	 * v1.0 zhanghc 2016-11-16下午10:13:48
	 * 
	 * @param id
	 * @return PageResult
	 */
	@RequestMapping("/del")
	@ResponseBody
	@RequiresRoles(value={"admin"},logical = Logical.OR)
	public PageResult del(Integer id) {
		try {
			File file = fileService.getEntity(id);
			file.setState(0);
			fileService.update(file);
			return PageResult.ok();
		} catch (MyException e) {
			log.error("完成删除附件错误：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("完成删除附件错误：", e);
			return PageResult.err();
		}
	}
}
