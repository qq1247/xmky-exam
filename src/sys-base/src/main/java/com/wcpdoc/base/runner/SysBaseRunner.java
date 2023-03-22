package com.wcpdoc.base.runner;

import java.io.File;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.base.cache.ParmCache;
import com.wcpdoc.base.entity.Parm;
import com.wcpdoc.base.service.ParmService;

/**
 * 系统基础启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class SysBaseRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(SysBaseRunner.class);
	@Resource
	private ParmService parmService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// 转换斜杠，保证linux下也正常
		Parm parm = parmService.getEntity(1);
		parm.setFileUploadDir(parm.getFileUploadDir().replace("\\", File.separator).replace("/", File.separator));
		parm.setDbBakDir(parm.getDbBakDir().replace("\\", File.separator).replace("/", File.separator));
		parmService.update(parm);
		
		// 缓存系统参数
		log.info("系统基础启动：缓存系统参数");
		ParmCache.flushCache(parm);
	}
}
