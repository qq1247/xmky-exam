package com.wcpdoc.quartz.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.core.env.Environment;

import com.wcpdoc.core.exception.MyException;
import com.wcpdoc.core.util.DateUtil;
import com.wcpdoc.core.util.SpringUtil;
import com.wcpdoc.quartz.service.CronExService;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库自动备份
 * 
 * v1.0 zhanghc 2021年10月13日下午2:08:45
 */
@Slf4j
public class DbBackJob implements Job {
	private static final String DB_URL = SpringUtil.getBean(Environment.class).getProperty("spring.datasource.url");
	private static final String DB_USERNAME = SpringUtil.getBean(Environment.class)
			.getProperty("spring.datasource.username");
	private static final String DB_PASSWORD = SpringUtil.getBean(Environment.class)
			.getProperty("spring.datasource.password");

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// 备份数据库
		log.info("数据库备份开始：");
		File dbBakDir = new File(SpringUtil.getBean(CronExService.class).getDbBakDir());
		dbBakDir.mkdirs();
		log.info("数据库备份目录：{}", dbBakDir.getAbsolutePath());

		List<String> bakCmd = new ArrayList<>(3);
		if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
			bakCmd.add("cmd");
			bakCmd.add("/c");
		} else {
			bakCmd.add("sh");
			bakCmd.add("-c");
		}
		bakCmd.add(String.format("mysqldump -h%s -p%s -u%s -p%s --default-character-set=%s %s > %s%s%s.sql",
				getIp(DB_URL), getPort(DB_URL), DB_USERNAME, DB_PASSWORD, getEncoding(DB_URL), getDbName(DB_URL),
				dbBakDir.getAbsolutePath(), File.separator, DateUtil.formatDate(new Date())));

		Process process = null;
		try {
			process = new ProcessBuilder(bakCmd).redirectErrorStream(true).start();// 错误流重定向到标准输入流，不管正常错误处理一个流就可以了
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				log.info("数据库备份命令行日志：{}", line);
			}

			boolean succ = process.waitFor(1, TimeUnit.SECONDS);
			if (!succ) {// processImpl实现类第一行if (getExitCodeProcess(handle) != STILL_ACTIVE) return true;
				throw new MyException("备份超时，中断执行");// 超时在下面判断，目前测试一直返回true，目前不影响正常业务。
			}

			if (process.exitValue() != 0) {
				throw new MyException("备份异常退出，请查看日志定位问题");
			}
		} catch (Exception e) {
			log.error("数据库备份失败：{}", e.getMessage());
			return;// 如果备份失败，就不继续往下执行，保留旧备份文件
		} finally {
			IOUtils.closeQuietly(process.getInputStream());
			IOUtils.closeQuietly(process.getOutputStream());
			IOUtils.closeQuietly(process.getErrorStream());
			process.destroy();
		}
		log.info("数据库备份成功");

		// 清除7天前的备份
		log.error("数据库备份清理开始");
		List<java.io.File> fileList = (List<java.io.File>) FileUtils.listFilesAndDirs(dbBakDir,
				FileFilterUtils.suffixFileFilter("sql"), TrueFileFilter.INSTANCE);
		Date beforTime = DateUtil.getNextDay(new Date(), -7);
		for (int i = 0; i < fileList.size() - 1; i++) {
			java.io.File file = fileList.get(fileList.size() - i - 1);
			if ((file.isDirectory() && file.listFiles().length == 0)
					|| (file.isFile() && FileUtils.isFileOlder(file, beforTime))) { // 如果是空目录或过期附件
				log.info("数据库备份清理：{}删除", file.getAbsolutePath());
				try {
					file.delete();
				} catch (Exception e) {
					log.error("数据库备份清理失败：", e);
				}
			}
		}
		log.info("数据库备份清理完成");
	}

	private String getDbName(String dbUrl) {
		// jdbc:log4jdbc:mysql://127.0.0.1:3306/exam?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8&useSSL=false
		return DB_URL.substring(DB_URL.lastIndexOf("/") + 1, DB_URL.lastIndexOf("?"));
	}

	private String getPort(String dbUrl) {
		return DB_URL.substring(DB_URL.lastIndexOf(":") + 1, DB_URL.lastIndexOf("/"));
	}

	private String getIp(String dbUrl) {
		return DB_URL.substring(DB_URL.indexOf("//") + 2, DB_URL.lastIndexOf(":"));
	}

	private String getEncoding(String dbUrl) {
		String _DB_URL = DB_URL.substring(DB_URL.indexOf("characterEncoding=") + 18);
		return _DB_URL.substring(0, _DB_URL.indexOf("&")).replace("-", "");// mysql\share\charsets\Index.xml指定了utf8，不能用utf-8
	}
}
