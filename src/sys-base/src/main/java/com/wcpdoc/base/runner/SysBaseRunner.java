package com.wcpdoc.base.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.base.service.BaseCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 系统基础启动
 * 
 * v1.0 zhanghc 2024-10-14下午09:50:12
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SysBaseRunner implements ApplicationRunner {
	@Value("${sys.ver}")
	private String sysVer;// 系统版本
	private final BaseCacheService baseCacheService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("系统基础启动：当前版本为{}，最新版本为{}", baseCacheService.getParm().getAppVer(),
				baseCacheService.getParm().getAppRelVer());
	}
}
