package com.wcpdoc.wordFilter.runner;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.wordFilter.service.SensitiveService;

/**
 * 初始化敏感词
 * 
 * v1.0 chenyun 2021年9月28日下午5:37:24
 */
@Component
public class SensitiveRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(SensitiveRunner.class);
	
	@Resource
	private SensitiveService sensitiveService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("启动监听：敏感词初始化");
		sensitiveService.init();
	}
}
