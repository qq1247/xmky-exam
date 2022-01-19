package com.wcpdoc.auth.runner;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.cache.JwtSecretCache;
import com.wcpdoc.core.util.StringUtil;

/**
 * 授权服务启动
 * 
 * v1.0 chenyun 2021年11月16日下午1:44:19
 */
@Component
public class AuthRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(AuthRunner.class);

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("启动监听：加载秘钥文件开始");
	    File jwtSecretFile = new File(String.format("./config/jwtSecret.txt"));
	    if (!jwtSecretFile.exists()) {
	    	log.info("启动监听：秘钥文件不存在，开始写入秘钥");
	    	String secret = StringUtil.getRandom(64);
	     	try (FileWriter writer = new FileWriter(jwtSecretFile)) {
		    	writer.write(secret);
		    } catch (Exception e) {
				log.error("启动监听：写入秘钥文件失败", e);
				return;
			}
	    }
	    
        String secret = FileUtils.readFileToString(jwtSecretFile, "UTF-8");
        log.info("启动监听：加载秘钥到缓冲");
    	JwtSecretCache.addCache(secret);
    	log.info("启动监听：加载秘钥成功");
	}
}