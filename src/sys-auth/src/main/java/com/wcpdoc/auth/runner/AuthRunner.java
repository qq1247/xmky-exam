package com.wcpdoc.auth.runner;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.cache.JwtSecretCache;
import com.wcpdoc.core.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统权限启动
 * 
 * v1.0 chenyun 2021年11月16日下午1:44:19
 */
@Component
@Slf4j
public class AuthRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 如果是第一次启动程序，生成秘钥文件
		File jwtSecretFile = new File(String.format(".%sconfig%sjwtSecret.txt", File.separator, File.separator));
		if (!jwtSecretFile.exists()) {
			log.info("系统权限启动：生成秘钥文件");
			String jwtSecret = StringUtil.getRandom(64);
			try {
				FileUtils.writeStringToFile(jwtSecretFile, jwtSecret, "utf-8");
			} catch (Exception e) {
				log.error("系统权限启动：写入秘钥失败", e);
			}
		}

		// 读取秘钥文件并缓存
		log.info("系统权限启动：读取秘钥文件并缓存");
		String jwtSecret = null;
		try {
			jwtSecret = FileUtils.readFileToString(jwtSecretFile, "utf-8");
		} catch (Exception e) {
			log.error("系统权限启动：读取秘钥失败，生成临时秘钥并缓存", e);
			jwtSecret = StringUtil.getRandom(64);
		}
		JwtSecretCache.flushCache(jwtSecret);
	}
}