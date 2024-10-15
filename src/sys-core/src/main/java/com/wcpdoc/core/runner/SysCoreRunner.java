package com.wcpdoc.core.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 考试核心启动
 * 
 * v1.0 zhanghc 2024-10-14下午09:50:12
 */
@Component
@Slf4j
public class SysCoreRunner implements ApplicationRunner {
	@Value("${sys.ver}")
	private String sysVer;// 系统版本

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("系统核心启动：当前版本为{}", sysVer);
	}
}
