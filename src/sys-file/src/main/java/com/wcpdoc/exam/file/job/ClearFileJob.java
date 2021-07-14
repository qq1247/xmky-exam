package com.wcpdoc.exam.file.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import com.wcpdoc.exam.core.util.DateUtil;
import com.wcpdoc.exam.core.util.SpringUtil;
import com.wcpdoc.exam.file.entity.File;
import com.wcpdoc.exam.file.service.FileService;

/**
 * 清理临时附件任务
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
public class ClearFileJob implements Job {
	private static final Logger log = LoggerFactory.getLogger(ClearFileJob.class);
	private static final FileService fileService = SpringUtil.getBean(FileService.class);
	private static final String fileUploadDir = SpringUtil.getBean(Environment.class).getProperty("file.upload.dir");

	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 清理临时附件
		String baseDir = fileUploadDir;
		java.io.File dirFile = new java.io.File(baseDir + java.io.File.separator + "temp");
		if (!dirFile.exists()) {
			log.info("清理临时附件：{}不存在", dirFile.getAbsolutePath());
			return;
		}
		List<java.io.File> fileList = (List<java.io.File>) FileUtils.listFilesAndDirs(dirFile, TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE);
		Date beforTime = DateUtil.getNextDay(new Date(), -7);

		for (int i = 0; i < fileList.size() - 1; i++) {
			java.io.File file = fileList.get(fileList.size() - i - 1);
			if ((file.isDirectory() && file.listFiles().length == 0)
					|| (file.isFile() && FileUtils.isFileOlder(file, beforTime))) { // 如果是空目录或过期附件
				log.info("清理临时附件：{}", file.getAbsolutePath());
				try {
					file.delete();
				} catch (Exception e) {
					log.error("清理临时附件失败：", e);
				}
			}
		}

		// 清理标记为删除的附件
		List<File> delFileList = fileService.getDelList();
		for (File fileEntity : delFileList) {
			java.io.File delFile = new java.io.File(baseDir + java.io.File.separator + fileEntity.getPath());
			log.info("清理标记为删除的附件：{}", delFile.getAbsolutePath());
			try {
				delFile.delete();
			} catch (Exception e) {
				log.error("清理标记为删除的附件失败：", e);
			}
			fileService.del(fileEntity.getId());
		}
	}
}
