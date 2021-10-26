package com.wcpdoc.exam.base.runner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.wcpdoc.exam.base.cache.DictCache;

/**
 * 基础服务启动
 * 
 * v1.0 zhanghc 2019年9月29日下午2:32:16
 */
@Component
public class BaseRunner implements ApplicationRunner {

	@Override
	public void run(ApplicationArguments args) throws Exception {
		DictCache.flushCache();
	}
}
