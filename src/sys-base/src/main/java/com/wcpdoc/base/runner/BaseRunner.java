package com.wcpdoc.base.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.base.cache.DictCache;
import com.wcpdoc.base.cache.ParmCache;

/**
 * 基础服务启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class BaseRunner implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(BaseRunner.class);

	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("启动监听：数据字典加入缓存");
		DictCache.flushCache();
		log.info("启动监听：系统参数加入缓存");
		ParmCache.flushCache();
	}
}
