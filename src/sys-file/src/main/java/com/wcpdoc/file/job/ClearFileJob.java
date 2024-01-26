package com.wcpdoc.file.job;

import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.file.entity.File;
import com.wcpdoc.file.service.FileService;

import lombok.extern.slf4j.Slf4j;

/**
 * 清理临时附件任务
 * 
 * v1.0 zhanghc 2016-11-16下午10:13:48
 */
@Slf4j
public class ClearFileJob implements Job {
	private static final FileService fileService = SpringUtil.getBean(FileService.class);

	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 清理临时附件
		String baseDir = fileService.getFileUploadDir();
		java.io.File dirFile = new java.io.File(String.format("%s%s%s", baseDir, java.io.File.separator, "temp"));
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
			java.io.File delFile = new java.io.File(
					String.format("%s%s%s", baseDir, java.io.File.separator, fileEntity.getPath()));
			log.info("清理标记为删除的附件：{}", delFile.getAbsolutePath());
			try {
				delFile.delete();
			} catch (Exception e) {
				log.error("清理标记为删除的附件失败：", e);
			}
			fileService.removeById(fileEntity.getId());
		}
	}
}
