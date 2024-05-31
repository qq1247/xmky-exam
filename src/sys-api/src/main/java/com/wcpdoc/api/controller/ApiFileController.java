package com.wcpdoc.api.controller;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.wcpdoc.core.controller.BaseController;
import com.wcpdoc.core.entity.PageResult;
import com.wcpdoc.core.entity.PageResultEx;
import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.file.entity.FileEx;
import com.wcpdoc.file.service.FileService;

import lombok.extern.slf4j.Slf4j;

/**
 * 附件控制层
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
@RestController
@RequestMapping("/api/file")
@Slf4j
public class ApiFileController extends BaseController {

	@Resource
	private FileService fileService;

	/**
	 * 附件上传（临时）
	 * 
	 * v1.0 zhanghc 2017年3月6日下午11:51:18
	 * 
	 * @param files
	 * @return PageResult
	 */
	@RequestMapping("/upload")
	public PageResult upload(@RequestParam("files") MultipartFile[] files, String uuid) {
		try {
			String[] allowTypes = { "jpg", "jpeg", "gif", "png", "zip", "rar", "doc", "xls", "docx", "xlsx", "mp4" };
			String fileIds = fileService.tempUpload(files, allowTypes, uuid);
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("fileIds", fileIds);
			return PageResultEx.ok().data(data);
		} catch (MyException e) {
			log.error("附件上传（临时）失败：{}", e.getMessage());
			return PageResult.err().msg(e.getMessage());
		} catch (Exception e) {
			log.error("附件上传（临时）失败：", e);
			return PageResult.err();
		}
	}

	/**
	 * 附件下载
	 * 
	 * v1.0 zhanghc 2017年3月29日下午10:18:28 <br/>
	 * 使用spring ResponseEntity <byte[]>方式，附件大会造成内存溢出。
	 * 
	 * @param id void
	 */
	@RequestMapping(value = "/download")
	public void download(Integer id) {
		try {
			FileEx fileEx = fileService.getFileEx(id);
			String fileName = new String(
					(fileEx.getEntity().getName() + "." + fileEx.getEntity().getExtName()).getBytes("UTF-8"),
					"ISO-8859-1");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.setContentType("application/force-download");
			try (OutputStream output = response.getOutputStream()) {
				FileUtils.copyFile(fileEx.getFile(), output);
			} catch (Exception e) {
				throw new MyException("拷贝文件错误");
			}
		} catch (MyException e) {
			log.error("附件下载失败：", e.getMessage());
		} catch (Exception e) {
			log.error("附件下载失败：", e);
		}
	}
}
