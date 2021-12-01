package com.wcpdoc.auth.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.auth.cache.JwtSecretCache;
import com.wcpdoc.core.util.StringUtil;

/**
 * jwtSecret缓存服务启动
 * 
 * v1.0 chenyun 2021年11月16日下午1:44:19
 */
@Component
public class JwtSecretRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(JwtSecretRunner.class);

	@SuppressWarnings("resource")
	@Override
	public void run(ApplicationArguments args) throws Exception {
	    String filepath = String.format(".\\config\\jwtSecret.txt");
	    File file = new File(filepath);
	    String random = null;
	    if (file.createNewFile()) {
	    	log.info("启动监听：自动生成令牌秘钥");
		    random = StringUtil.getRandom(64);
	     	try (FileWriter writer = new FileWriter(file)) {
		     writer.write(random);
		    }
	     	JwtSecretCache.addCache(random);
	    } else {
	    	log.info("启动监听：加载令牌秘钥到缓存");
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer sb = new StringBuffer();
            String text = null;
            while((text = bufferedReader.readLine()) != null){
                sb.append(text);
            }
	    	JwtSecretCache.addCache(sb.toString());
	    }
	}
}